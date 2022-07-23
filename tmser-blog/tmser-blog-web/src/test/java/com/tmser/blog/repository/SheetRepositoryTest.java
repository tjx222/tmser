package com.tmser.blog.repository;


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import com.tmser.blog.model.entity.Sheet;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.model.sort.Sort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.PrePersist;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Page repository test.
 *
 * @author johnniang
 * @date 3/22/19
 */
//@SpringBootTest
@Slf4j
class SheetRepositoryTest {

    @Autowired
    SheetRepository sheetRepository;

   // @Test
    void listAllTest() {
       // long count = sheetRepository.countVisit();
        Sort idDesc = Sort.by("id");
        boolean allByStatus = sheetRepository.existsByIdNotAndSlug(2,"about");
        System.out.println("----------count: "+allByStatus);
    }

    class TrieNode{

        TrieNode(char[] chars){
            if(chars.length == 0){
                isEnd = true;
            }else {
                childNodes = new HashMap<>();
                childNodes.put(chars[0], new TrieNode(Arrays.copyOfRange(chars,1, chars.length)));
            }
        }

        TrieNode(){}
        void add(char[] chars){
            if(chars.length == 0){  //已经存在
                if(isEnd == false){
                    isEnd = true;
                }
            }else {
                char c = chars[0];
                if(childNodes == null){
                    childNodes = new HashMap<>();
                }
                TrieNode next = childNodes.get(c);
                if(next == null){
                    childNodes.put(chars[0], new TrieNode(Arrays.copyOfRange(chars,1, chars.length)));
                    return;
                }

                next.add(Arrays.copyOfRange(chars,1,chars.length));
            }
        }


        boolean isEnd = false;
        Map<Character,TrieNode> childNodes;

        boolean exist(char[] chars){
            if(chars.length == 0){
                return isEnd;
            }
            char c = chars[0];
            if(childNodes == null){
                return false;
            }
            TrieNode next = childNodes.get(c);
            if(next == null){
                return false;
            }
            return next.exist(Arrays.copyOfRange(chars,1,chars.length));
        }

        boolean isEnd(){
            return isEnd;
        }
    }

    class Trie{
        TrieNode root = new TrieNode();

        void addText(String text){
            char[] chars = text.toCharArray();
            if(chars.length > 0){
                root.add(chars);
                return;
            }
        }

        boolean search(String text){
            return text.length() !=0 && root.exist(text.toCharArray());
        }
    }


    @Test
    void testMethod() throws Exception{
       /* List<Method> methodsListWithAnnotation = MethodUtils.getMethodsListWithAnnotation(Sheet.class, PrePersist.class, true,true);
        System.out.println(methodsListWithAnnotation);
        Sheet s = new Sheet();
        Method method = methodsListWithAnnotation.get(0);
        method.setAccessible(true);
        method.invoke(s,null);
        System.out.println(s);*/

      /*  String text = "重载不是重担,弟弟长高了，头发长长了";
        System.out.println(HanLP.newSegment("crf").seg(text));
        List<Pinyin> pinyinList = HanLP.convertToPinyinList(text);
        System.out.print("原文,");
        for (char c : text.toCharArray())
        {
            System.out.printf("%c,", c);
        }
        System.out.println();

        System.out.print("拼音（数字音调）,");
        for (Pinyin pinyin : pinyinList)
        {
            System.out.printf("%s,", pinyin);
        }
        System.out.println();

        System.out.print("拼音（符号音调）,");
        for (Pinyin pinyin : pinyinList)
        {
            System.out.printf("%s,", pinyin.getPinyinWithToneMark());
        }
        System.out.println();

        System.out.print("拼音（无音调）,");
        for (Pinyin pinyin : pinyinList)
        {
            System.out.printf("%s,", pinyin.getPinyinWithoutTone());
        }
        System.out.println();

        System.out.print("声调,");
        for (Pinyin pinyin : pinyinList)
        {
            System.out.printf("%s,", pinyin.getTone());
        }
        System.out.println();

        System.out.print("声母,");
        for (Pinyin pinyin : pinyinList)
        {
            System.out.printf("%s,", pinyin.getShengmu());
        }
        System.out.println();

        System.out.print("韵母,");
        for (Pinyin pinyin : pinyinList)
        {
            System.out.printf("%s,", pinyin.getYunmu());
        }
        System.out.println();

        System.out.print("输入法头,");
        for (Pinyin pinyin : pinyinList)
        {
            System.out.printf("%s,", pinyin.getHead());
        }
        System.out.println();*/
        Trie trie = new Trie();
        trie.addText("t");
        trie.addText("to");
        trie.addText("too");
        trie.addText("tom");
        System.out.println(trie.search("t"));
        System.out.println(trie.search("to"));
        System.out.println(trie.search("too"));
        System.out.println(trie.search("tol"));
        System.out.println(trie.search("tool"));
    }
}
