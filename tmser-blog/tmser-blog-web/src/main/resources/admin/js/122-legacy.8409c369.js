(self["webpackChunkhalo_admin"]=self["webpackChunkhalo_admin"]||[]).push([[122],{24122:function(e,t,l){"use strict";l.r(t),l.d(t,{default:function(){return k}});var a=function(){var e=this,t=e.$createElement,l=e._self._c||t;return l("page-view",{attrs:{title:e.activatedTheme?e.activatedTheme.name:"无",affix:"",subTitle:"当前启用"}},[l("template",{slot:"extra"},[l("a-button",{attrs:{loading:e.list.loading,icon:"reload"},on:{click:e.handleRefreshThemesCache}},[e._v(" 刷新")]),l("a-button",{attrs:{icon:"plus",type:"primary"},on:{click:function(t){e.installModal.visible=!0}}},[e._v(" 安装")])],1),l("a-row",{attrs:{gutter:12,align:"middle",type:"flex"}},[l("a-col",{attrs:{span:24}},[l("a-list",{attrs:{dataSource:e.sortedThemes,grid:{gutter:12,xs:1,sm:1,md:2,lg:4,xl:4,xxl:4},loading:e.list.loading},scopedSlots:e._u([{key:"renderItem",fn:function(t,a){return l("a-list-item",{key:a},[l("a-card",{attrs:{bodyStyle:{padding:0},title:t.name,hoverable:""}},[l("div",{staticClass:"theme-screenshot"},[l("img",{attrs:{alt:t.name,src:t.screenshots||"/images/placeholder.jpg",loading:"lazy"}})]),l("template",{slot:"actions"},[t.activated?l("div",[l("a-icon",{staticStyle:{"margin-right":"3px"},attrs:{theme:"twoTone",type:"unlock"}}),e._v(" 已启用 ")],1):l("div",{on:{click:function(l){return e.handleActiveTheme(t)}}},[l("a-icon",{staticStyle:{"margin-right":"3px"},attrs:{type:"lock"}}),e._v(" 启用 ")],1),l("div",{on:{click:function(l){return e.handleRouteToThemeSetting(t)}}},[l("a-icon",{staticStyle:{"margin-right":"3px"},attrs:{type:"setting"}}),e._v(" 设置 ")],1),l("a-dropdown",{attrs:{trigger:["click"],placement:"topCenter"}},[l("a",{staticClass:"ant-dropdown-link",attrs:{href:"#"}},[l("a-icon",{staticStyle:{"margin-right":"3px"},attrs:{type:"ellipsis"}}),e._v(" 更多 ")],1),l("a-menu",{attrs:{slot:"overlay"},slot:"overlay"},[l("a-menu-item",{key:1,attrs:{disabled:t.activated},on:{click:function(l){return e.handleOpenThemeDeleteModal(t)}}},[l("a-icon",{staticStyle:{"margin-right":"3px"},attrs:{type:"delete"}}),e._v(" 删除 ")],1),t.repo?l("a-menu-item",{key:2,on:{click:function(l){return e.handleConfirmRemoteUpdate(t)}}},[l("a-icon",{staticStyle:{"margin-right":"3px"},attrs:{type:"cloud"}}),e._v(" 在线更新 ")],1):e._e(),l("a-menu-item",{key:3,on:{click:function(l){return e.handleOpenLocalUpdateModal(t)}}},[l("a-icon",{staticStyle:{"margin-right":"3px"},attrs:{type:"file"}}),e._v(" 本地更新 ")],1)],1)],1)],1)],2)],1)}}])})],1)],1),l("ThemeDeleteConfirmModal",{attrs:{theme:e.themeDeleteModal.selected,visible:e.themeDeleteModal.visible},on:{"update:visible":function(t){return e.$set(e.themeDeleteModal,"visible",t)},onAfterClose:function(t){e.themeDeleteModal.selected={}},success:e.handleListThemes}}),l("ThemeLocalUpgradeModal",{attrs:{theme:e.localUpgradeModel.selected,visible:e.localUpgradeModel.visible},on:{"update:visible":function(t){return e.$set(e.localUpgradeModel,"visible",t)},onAfterClose:function(t){e.localUpgradeModel.selected={}},success:e.handleListThemes}}),l("ThemeInstallModal",{attrs:{visible:e.installModal.visible},on:{"update:visible":function(t){return e.$set(e.installModal,"visible",t)},onAfterClose:e.handleListThemes}})],2)},n=[],i=l(91057),r=(l(70315),l(96153),l(19003),l(31875),l(21082),l(43348)),o=l(59464),s=function(){var e=this,t=e.$createElement,l=e._self._c||t;return l("a-modal",{attrs:{afterClose:e.onModalClose,bodyStyle:{padding:"0 24px 24px"},footer:null,destroyOnClose:"",title:"安装主题"},model:{value:e.modalVisible,callback:function(t){e.modalVisible=t},expression:"modalVisible"}},[l("div",{staticClass:"custom-tab-wrapper"},[l("a-tabs",{attrs:{animated:{inkBar:!0,tabPane:!1}}},[l("a-tab-pane",{key:"1",attrs:{tab:"本地上传"}},[l("FilePondUpload",{ref:"upload",attrs:{accepts:["application/x-zip","application/x-zip-compressed","application/zip"],uploadHandler:e.local.uploadHandler,label:"点击选择主题包或将主题包拖拽到此处<br>仅支持 ZIP 格式的文件",name:"file"},on:{success:e.onUploadSucceed}}),l("div",{staticClass:"mt-5"},[l("a-alert",{attrs:{closable:"",type:"info"}},[l("template",{slot:"message"},[e._v(" 更多主题请访问： "),l("a",{attrs:{href:"https://halo.run/themes.html",target:"_blank"}},[e._v("https://halo.run/themes")])])],2)],1)],1),l("a-tab-pane",{key:"2",attrs:{tab:"远程下载"}},[l("a-form-model",{ref:"remoteInstallForm",attrs:{model:e.remote,rules:e.remote.rules,layout:"vertical"}},[l("a-form-model-item",{attrs:{help:"* 支持 Git 仓库地址，ZIP 链接。",label:"远程地址：",prop:"url"}},[l("a-input",{model:{value:e.remote.url,callback:function(t){e.$set(e.remote,"url",t)},expression:"remote.url"}})],1),l("a-form-model-item",[l("ReactiveButton",{attrs:{errored:e.remote.fetchErrored,loading:e.remote.fetching,erroredText:"下载失败",loadedText:"下载成功",text:"下载",type:"primary"},on:{callback:e.handleRemoteFetchCallback,click:e.handleRemoteFetching}})],1)],1),l("div",{staticClass:"mt-5"},[l("a-alert",{attrs:{closable:"",type:"info"}},[l("template",{slot:"message"},[e._v(" 目前仅支持远程 Git 仓库和 ZIP 下载链接。更多主题请访问： "),l("a",{attrs:{href:"https://halo.run/themes.html",target:"_blank"}},[e._v("https://halo.run/themes")])])],2)],1)],1)],1)],1)])},c=[],d=l(1540),u={name:"ThemeInstallModal",props:{visible:{type:Boolean,default:!1}},data:function(){return{local:{uploadHandler:function(e,t){return d.Z.theme.upload(e,t)}},remote:{url:null,fetching:!1,fetchErrored:!1,rules:{url:[{required:!0,message:"* 远程地址不能为空",trigger:["change"]}]}}}},computed:{modalVisible:{get:function(){return this.visible},set:function(e){this.$emit("update:visible",e)}}},methods:{onModalClose:function(){this.$refs.upload.handleClearFileList(),this.remote.url=null,this.$emit("onAfterClose")},onUploadSucceed:function(){this.modalVisible=!1,this.$emit("upload-succeed")},handleRemoteFetching:function(){var e=this;this.$refs.remoteInstallForm.validate(function(){var t=(0,i.Z)(regeneratorRuntime.mark((function t(l){return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:if(!l){t.next=14;break}return t.prev=1,e.remote.fetching=!0,t.next=5,d.Z.theme.fetchTheme(e.remote.url);case 5:t.next=11;break;case 7:t.prev=7,t.t0=t["catch"](1),e.remote.fetchErrored=!0,e.$log.error("Fetch remote theme failed: ",t.t0);case 11:return t.prev=11,setTimeout((function(){e.remote.fetching=!1}),400),t.finish(11);case 14:case"end":return t.stop()}}),t,null,[[1,7,11,14]])})));return function(e){return t.apply(this,arguments)}}())},handleRemoteFetchCallback:function(){this.remote.fetchErrored?this.remote.fetchErrored=!1:this.modalVisible=!1}}},h=u,m=l(18156),f=(0,m.Z)(h,s,c,!1,null,null,null),p=f.exports,v=l(50990),g={components:{PageView:v.B4,ThemeDeleteConfirmModal:r.Z,ThemeLocalUpgradeModal:o.Z,ThemeInstallModal:p},data:function(){return{list:{loading:!1,data:[]},installModal:{visible:!1},localUpgradeModel:{visible:!1,selected:{}},themeDeleteModal:{visible:!1,selected:{}}}},computed:{sortedThemes:function(){var e=this.list.data.slice(0);return e.sort((function(e,t){return t.activated-e.activated}))},activatedTheme:function(){return this.sortedThemes.length>0?this.sortedThemes[0]:null}},beforeMount:function(){this.handleListThemes()},methods:{handleListThemes:function(){var e=this;this.list.loading=!0,d.Z.theme.list().then((function(t){e.list.data=t.data})).finally((function(){e.list.loading=!1}))},handleRefreshThemesCache:function(){var e=this;d.Z.theme.reload().finally((function(){e.handleListThemes()}))},handleActiveTheme:function(e){var t=this;d.Z.theme.active(e.id).finally((function(){t.handleListThemes()}))},handleOpenLocalUpdateModal:function(e){this.localUpgradeModel.selected=e,this.localUpgradeModel.visible=!0},handleRouteToThemeSetting:function(e){this.$router.push({name:"ThemeSetting",query:{themeId:e.id}})},handleOpenThemeDeleteModal:function(e){this.themeDeleteModal.visible=!0,this.themeDeleteModal.selected=e},handleConfirmRemoteUpdate:function(e){var t=this;t.$confirm({title:"提示",maskClosable:!0,content:"确定更新【"+e.name+"】主题？",onOk:function(){return(0,i.Z)(regeneratorRuntime.mark((function l(){var a;return regeneratorRuntime.wrap((function(l){while(1)switch(l.prev=l.next){case 0:return a=t.$message.loading("更新中...",0),l.prev=1,l.next=4,d.Z.theme.updateThemeByFetching(e.id);case 4:t.$message.success("更新成功！"),l.next=10;break;case 7:l.prev=7,l.t0=l["catch"](1),t.$log.error("Failed to update theme: ",l.t0);case 10:return l.prev=10,a(),t.handleListThemes(),l.finish(10);case 14:case"end":return l.stop()}}),l,null,[[1,7,10,14]])})))()}})}}},b=g,y=(0,m.Z)(b,a,n,!1,null,null,null),k=y.exports},43348:function(e,t,l){"use strict";l.d(t,{Z:function(){return u}});var a=function(){var e=this,t=e.$createElement,l=e._self._c||t;return l("a-modal",{attrs:{afterClose:e.onAfterClose,closable:!1,width:416,destroyOnClose:"",title:"提示"},model:{value:e.modalVisible,callback:function(t){e.modalVisible=t},expression:"modalVisible"}},[l("template",{slot:"footer"},[l("a-button",{on:{click:function(t){e.modalVisible=!1}}},[e._v(" 取消 ")]),l("ReactiveButton",{attrs:{errored:e.deleteErrored,loading:e.deleting,erroredText:"删除失败",loadedText:"删除成功",text:"确定"},on:{callback:e.handleDeleteCallback,click:function(t){return e.handleDelete()}}})],1),l("p",[e._v("确定删除【"+e._s(e.theme.name)+"】主题？")]),l("a-checkbox",{model:{value:e.deleteSettings,callback:function(t){e.deleteSettings=t},expression:"deleteSettings"}},[e._v(" 同时删除主题配置 ")])],2)},n=[],i=l(91057),r=(l(70315),l(1540)),o={name:"ThemeDeleteConfirmModal",props:{visible:{type:Boolean,default:!1},theme:{type:Object,default:function(){return{}}}},data:function(){return{deleteErrored:!1,deleting:!1,deleteSettings:!1}},computed:{modalVisible:{get:function(){return this.visible},set:function(e){this.$emit("update:visible",e)}}},methods:{handleDelete:function(){var e=this;return(0,i.Z)(regeneratorRuntime.mark((function t(){return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.prev=0,e.deleting=!0,t.next=4,r.Z.theme["delete"](e.theme.id,e.deleteSettings);case 4:t.next=10;break;case 6:t.prev=6,t.t0=t["catch"](0),e.deleteErrored=!1,e.$log.error("Delete theme failed",t.t0);case 10:return t.prev=10,setTimeout((function(){e.deleting=!1}),400),t.finish(10);case 13:case"end":return t.stop()}}),t,null,[[0,6,10,13]])})))()},handleDeleteCallback:function(){this.deleteErrored?this.deleteErrored=!1:(this.modalVisible=!1,this.$emit("success"))},onAfterClose:function(){this.deleteErrored=!1,this.deleting=!1,this.deleteSettings=!1,this.$emit("onAfterClose")}}},s=o,c=l(18156),d=(0,c.Z)(s,a,n,!1,null,null,null),u=d.exports},59464:function(e,t,l){"use strict";l.d(t,{Z:function(){return d}});var a=function(){var e=this,t=e.$createElement,l=e._self._c||t;return l("a-modal",{attrs:{afterClose:e.onModalClose,footer:null,destroyOnClose:"",title:"更新主题"},model:{value:e.modalVisible,callback:function(t){e.modalVisible=t},expression:"modalVisible"}},[l("FilePondUpload",{ref:"updateByFile",attrs:{accepts:["application/x-zip","application/x-zip-compressed","application/zip"],field:e.theme.id,multiple:!1,uploadHandler:e.uploadHandler,label:"点击选择主题更新包或将主题更新包拖拽到此处<br>仅支持 ZIP 格式的文件",name:"file"},on:{success:e.onThemeUploadSuccess}})],1)},n=[],i=l(1540),r={name:"ThemeLocalUpgradeModal",props:{visible:{type:Boolean,default:!1},theme:{type:Object,default:function(){return{}}}},data:function(){return{uploadHandler:function(e,t,l){return i.Z.theme.updateByUpload(e,t,l)}}},computed:{modalVisible:{get:function(){return this.visible},set:function(e){this.$emit("update:visible",e)}}},methods:{onModalClose:function(){this.$refs.updateByFile.handleClearFileList(),this.$emit("onAfterClose")},onThemeUploadSuccess:function(){this.modalVisible=!1,this.$emit("success")}}},o=r,s=l(18156),c=(0,s.Z)(o,a,n,!1,null,null,null),d=c.exports},53303:function(e,t,l){var a=l(15820),n=Math.floor,i=function(e,t){var l=e.length,s=n(l/2);return l<8?r(e,t):o(e,i(a(e,0,s),t),i(a(e,s),t),t)},r=function(e,t){var l,a,n=e.length,i=1;while(i<n){a=i,l=e[i];while(a&&t(e[a-1],l)>0)e[a]=e[--a];a!==i++&&(e[a]=l)}return e},o=function(e,t,l,a){var n=t.length,i=l.length,r=0,o=0;while(r<n||o<i)e[r+o]=r<n&&o<i?a(t[r],l[o])<=0?t[r++]:l[o++]:r<n?t[r++]:l[o++];return e};e.exports=i},52103:function(e,t,l){var a=l(82678),n=a.match(/firefox\/(\d+)/i);e.exports=!!n&&+n[1]},76044:function(e,t,l){var a=l(82678);e.exports=/MSIE|Trident/.test(a)},97551:function(e,t,l){var a=l(82678),n=a.match(/AppleWebKit\/(\d+)\./);e.exports=!!n&&+n[1]},19003:function(e,t,l){"use strict";var a=l(79644),n=l(33691),i=l(77925),r=l(43207),o=l(71768),s=l(43150),c=l(32640),d=l(53303),u=l(29415),h=l(52103),m=l(76044),f=l(3718),p=l(97551),v=[],g=n(v.sort),b=n(v.push),y=c((function(){v.sort(void 0)})),k=c((function(){v.sort(null)})),x=u("sort"),T=!c((function(){if(f)return f<70;if(!(h&&h>3)){if(m)return!0;if(p)return p<603;var e,t,l,a,n="";for(e=65;e<76;e++){switch(t=String.fromCharCode(e),e){case 66:case 69:case 70:case 72:l=3;break;case 68:case 71:l=4;break;default:l=2}for(a=0;a<47;a++)v.push({k:t+a,v:l})}for(v.sort((function(e,t){return t.v-e.v})),a=0;a<v.length;a++)t=v[a].k.charAt(0),n.charAt(n.length-1)!==t&&(n+=t);return"DGBEFHACIJK"!==n}})),C=y||!k||!x||!T,M=function(e){return function(t,l){return void 0===l?-1:void 0===t?1:void 0!==e?+e(t,l)||0:s(t)>s(l)?1:-1}};a({target:"Array",proto:!0,forced:C},{sort:function(e){void 0!==e&&i(e);var t=r(this);if(T)return void 0===e?g(t):g(t,e);var l,a,n=[],s=o(t);for(a=0;a<s;a++)a in t&&b(n,t[a]);d(n,M(e)),l=n.length,a=0;while(a<l)t[a]=n[a++];while(a<s)delete t[a++];return t}})}}]);