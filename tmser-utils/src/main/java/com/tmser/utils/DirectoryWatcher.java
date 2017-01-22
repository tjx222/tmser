/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.utils;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * <pre>
 * 目录监控器，支持监控子目录
 * </pre>
 *
 * @author tmser
 * @version $Id: DirectoryWatcher.java, v 1.0 2016年2月16日 下午3:43:27 tmser Exp $
 */
public class DirectoryWatcher extends Observable{
	private static final Logger logger = LoggerFactory.getLogger(DirectoryWatcher.class);
	private WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private final boolean recursive;
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    FutureTask<Integer> task = new FutureTask<Integer>(
            new Callable<Integer>(){
                @Override
				public Integer call() throws InterruptedException{
                    	processEvents();
                    	return Integer.valueOf(0);
                    }
                }
            );
    
    public DirectoryWatcher(String dir) throws IOException {
    	this(dir,false);
    }
    
    /**
     * Creates a WatchService and registers the given directory
     */
   public DirectoryWatcher(String dir, boolean recursive) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey,Path>();
        this.recursive = recursive;

        if (recursive) {
        	logger.debug("Scanning {} ...", dir);
            registerAll(Paths.get(dir));
            logger.debug("Scanning Done.");
        } else {
            register(Paths.get(dir));
        }
    }


    /**
     * 启动监控过程
     */
    public void execute(){
        // 通过线程池启动一个额外的线程加载Watching过程
        executor.execute(task);        
    }
    
    /**
     * 关闭后的对象无法重新启动
     * @throws IOException
     */
    public void shutdown() throws IOException {
        watcher.close();
        executor.shutdown();
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
          Path prev = keys.get(key);
            if (prev == null) {
                logger.trace("register: {}", dir);
            } else {
                if (!dir.equals(prev)) {
                    logger.trace("update: {} -> {}", prev, dir);
                }
            }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
        for (;;) {
            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
            	logger.warn("WatchKey not recognized!");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                Kind<?> kind = event.kind();
                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                logger.debug("{} : {}", ev.kind().name(), child);
                notifiy(name, kind);

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }
    
    /**
     * 通知外部各个Observer目录有新的事件更新
     */
    void notifiy(Path name, Kind<?> kind){
        // 标注目录已经被做了更改
        setChanged();
        //     主动通知各个观察者目标对象状态的变更
        //    这里采用的是观察者模式的“推”方式
        notifyObservers(new FileSystemEventArgs(name, kind));
    }

    static void usage() {
        System.err.println("usage: java WatchDir [-r] dir");
        System.exit(-1);
    }

   public static void main(String[] args) throws IOException {
        // parse arguments
        if (args.length == 0 || args.length > 2)
            usage();
        boolean recursive = false;
        int dirArg = 0;
        if (args[0].equals("-r")) {
            if (args.length < 2)
                usage();
            recursive = true;
            dirArg++;
        }

        // register directory and process its events
        DirectoryWatcher dw =  new DirectoryWatcher(args[dirArg], recursive);
        dw.execute();
        try {
        	
			Thread.sleep(20000);
			dw.shutdown();
			System.out.println("shutdown");
			Thread.sleep(2000);
			System.out.println("close");
		} catch (InterruptedException e) {
			logger.error("", e);
		}
    }
    
    
    public static class FileSystemEventArgs{

    	private Path file;
    	
    	private Kind<?> kind;
    	/**
    	 * @param filename
    	 * @param kind
    	 */
    	public FileSystemEventArgs(Path file, Kind<?> kind) {
    		
    		this.file = file;
    		this.kind = kind;
    	}
    	public Kind<?> getKind() {
    		return kind;
    	}
    	public void setKind(Kind<?> kind) {
    		this.kind = kind;
    	}
    	public Path getFile() {
    		return file;
    	}
    	public void setFile(Path file) {
    		this.file = file;
    	}
    	
    }

  }

