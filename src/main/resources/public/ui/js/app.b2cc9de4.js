(function(t){function e(e){for(var s,i,l=e[0],o=e[1],c=e[2],u=0,p=[];u<l.length;u++)i=l[u],Object.prototype.hasOwnProperty.call(n,i)&&n[i]&&p.push(n[i][0]),n[i]=0;for(s in o)Object.prototype.hasOwnProperty.call(o,s)&&(t[s]=o[s]);d&&d(e);while(p.length)p.shift()();return r.push.apply(r,c||[]),a()}function a(){for(var t,e=0;e<r.length;e++){for(var a=r[e],s=!0,l=1;l<a.length;l++){var o=a[l];0!==n[o]&&(s=!1)}s&&(r.splice(e--,1),t=i(i.s=a[0]))}return t}var s={},n={app:0},r=[];function i(e){if(s[e])return s[e].exports;var a=s[e]={i:e,l:!1,exports:{}};return t[e].call(a.exports,a,a.exports,i),a.l=!0,a.exports}i.m=t,i.c=s,i.d=function(t,e,a){i.o(t,e)||Object.defineProperty(t,e,{enumerable:!0,get:a})},i.r=function(t){"undefined"!==typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(t,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(t,"__esModule",{value:!0})},i.t=function(t,e){if(1&e&&(t=i(t)),8&e)return t;if(4&e&&"object"===typeof t&&t&&t.__esModule)return t;var a=Object.create(null);if(i.r(a),Object.defineProperty(a,"default",{enumerable:!0,value:t}),2&e&&"string"!=typeof t)for(var s in t)i.d(a,s,function(e){return t[e]}.bind(null,s));return a},i.n=function(t){var e=t&&t.__esModule?function(){return t["default"]}:function(){return t};return i.d(e,"a",e),e},i.o=function(t,e){return Object.prototype.hasOwnProperty.call(t,e)},i.p="/ui/";var l=window["webpackJsonp"]=window["webpackJsonp"]||[],o=l.push.bind(l);l.push=e,l=l.slice();for(var c=0;c<l.length;c++)e(l[c]);var d=o;r.push([0,"chunk-vendors"]),a()})({0:function(t,e,a){t.exports=a("56d7")},1439:function(t,e,a){"use strict";var s=a("b278"),n=a.n(s);n.a},"1d88":function(t,e,a){},4403:function(t,e,a){},"56d7":function(t,e,a){"use strict";a.r(e);a("e260"),a("e6cf"),a("cca6"),a("a79d");var s=a("2b0e"),n=a("b970"),r=(a("157a"),a("8c4f")),i=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"row"},[a("div",{staticClass:"col-md-8 col-md-offset-2"},[a("div",{staticClass:"panel panel-default"},[t._m(0),t.loading?a("van-skeleton",{staticClass:"van-skeleton",attrs:{title:"",row:8}}):t._e(),t.loading?t._e():a("table",{staticClass:"table table-hover"},[a("tbody",t._l(t.jvms,(function(e){return a("tr",{key:e.vmid},[t._m(1,!0),a("td",{staticClass:"col-md-10"},[a("router-link",{attrs:{to:"/jvm/"+e.vmid+"/vm"}},[a("div",[t._v(t._s(e.vmid))]),a("div",{staticStyle:{"margin-top":"4px","word-break":"break-all"}},[t._v(" "+t._s(e.displayName)+" ")])])],1)])})),0)])],1)])])},l=[function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"panel-heading"},[a("h3",{staticClass:"panel-title"},[t._v("JVM")])])},function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("td",{staticClass:"col-md-2"},[a("div",[t._v("PID:")]),a("div",{staticStyle:{"margin-top":"4px"}},[t._v("Name:")])])}],o=(a("96cf"),a("1da1")),c=a("bc3a"),d={name:"Home",data:function(){return{loading:!0,jvms:null}},methods:{getJVM:function(){var t=Object(o["a"])(regeneratorRuntime.mark((function t(){var e;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,c.get("/api/jvms").catch((function(t){console.log(t)}));case 2:e=t.sent,this.jvms=e.data,this.loading=!1;case 5:case"end":return t.stop()}}),t,this)})));function e(){return t.apply(this,arguments)}return e}()},created:function(){this.getJVM()}},u=d,p=(a("bc98"),a("2877")),v=Object(p["a"])(u,i,l,!1,null,"8d713dd8",null),m=v.exports,h=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"row"},[a("div",{staticClass:"col-md-2"},[a("table",{staticClass:"table table-hover card"},[a("tbody",[a("tr",[a("div",[a("router-link",{attrs:{to:"/jvm/"+t.id+"/vm"}},[t._v("VM概要")])],1)]),a("tr",[a("div",[a("router-link",{attrs:{to:"/jvm/"+t.id+"/memory"}},[t._v("内存")])],1)]),a("tr",[a("div",[a("router-link",{attrs:{to:"/jvm/"+t.id+"/thread"}},[t._v("线程")])],1)]),a("tr",[a("div",[a("router-link",{attrs:{to:"/jvm/"+t.id+"/objects"}},[t._v("堆对象统计")])],1)])])])]),a("div",{staticClass:"col-md-10"},[a("div",{staticClass:"card"},[a("router-view")],1)])])},f=[],g={name:"Detail",data:function(){return{id:null,loading:!0}},methods:{},created:function(){this.id=this.$route.params.id}},b=g,_=(a("97d9"),Object(p["a"])(b,h,f,!1,null,"c03a4f6a",null)),w=_.exports,C=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"content"},[a("p",{staticClass:"head"},[t._v("VM概要")]),t.loading?a("van-skeleton",{staticClass:"van-skeleton",attrs:{title:"",row:8}}):t._e(),t.loading?t._e():a("div",[a("el-row",[a("el-col",{attrs:{span:4}},[a("div",{staticClass:"grid-content bg-purple"},[t._v("主类")])]),a("el-col",{attrs:{span:20}},[a("div",{staticClass:"grid-content bg-purple-light item-content"},[t._v(" "+t._s(t.data.mainClass)+" ")])])],1),a("el-divider"),a("el-row",[a("el-col",{attrs:{span:4}},[a("div",{staticClass:"grid-content bg-purple"},[t._v("运行时间")])]),a("el-col",{attrs:{span:20}},[a("div",{staticClass:"grid-content bg-purple-light item-content"},[t._v(" "+t._s((t.data.upTime/1e3).toFixed(2))+"秒 ")])])],1),a("el-divider"),a("el-row",[a("el-col",{attrs:{span:4}},[a("div",{staticClass:"grid-content bg-purple"},[t._v("进程CPU时间")])]),a("el-col",{attrs:{span:20}},[a("div",{staticClass:"grid-content bg-purple-light item-content"},[t._v(" "+t._s((t.data.progressCpuTime/1e3).toFixed(3))+" 秒 ")])])],1),a("el-divider"),a("el-row",[a("el-col",{attrs:{span:4}},[a("div",{staticClass:"grid-content bg-purple"},[t._v("当前堆大小")])]),a("el-col",{attrs:{span:20}},[a("div",{staticClass:"grid-content bg-purple-light item-content"},[t._v(" "+t._s(t.data.heapUsed.toFixed(2))+" MB ")])])],1),a("el-divider"),a("el-row",[a("el-col",{attrs:{span:4}},[a("div",{staticClass:"grid-content bg-purple"},[t._v("最大堆大小")])]),a("el-col",{attrs:{span:20}},[a("div",{staticClass:"grid-content bg-purple-light item-content"},[t._v(" "+t._s(t.data.heapMax.toFixed(2))+" MB ")])])],1),a("el-divider"),a("el-row",[a("el-col",{attrs:{span:4}},[a("div",{staticClass:"grid-content bg-purple"},[t._v("提交的内存")])]),a("el-col",{attrs:{span:20}},[a("div",{staticClass:"grid-content bg-purple-light item-content"},[t._v(" "+t._s(t.data.heapCommitted.toFixed(2))+" MB ")])])],1),a("el-divider"),a("el-row",[a("el-col",{attrs:{span:4}},[a("div",{staticClass:"grid-content bg-purple"},[t._v("GC")])]),a("el-col",{attrs:{span:20}},t._l(t.data.garbageCollectInfos,(function(e){return a("div",{key:e.name,staticClass:"grid-content bg-purple-light item-content"},[t._v(" "+t._s(e.name)+"收集:"+t._s(e.count)+", 耗时:"+t._s((e.time/1e3).toFixed(3))+" 秒 ")])})),0)],1),a("el-divider"),a("el-row",[a("el-col",{attrs:{span:4}},[a("div",{staticClass:"grid-content bg-purple"},[t._v("虚拟机")])]),a("el-col",{attrs:{span:20}},[a("div",{staticClass:"grid-content bg-purple-light item-content"},[t._v(" "+t._s(t.data.vmName)+" 版本 "+t._s(t.data.vmVersion)+" ")])])],1),a("el-divider"),a("el-row",[a("el-col",{attrs:{span:4}},[a("div",{staticClass:"grid-content bg-purple"},[t._v("处理器数")])]),a("el-col",{attrs:{span:20}},[a("div",{staticClass:"grid-content bg-purple-light item-content"},[t._v(" "+t._s(t.data.availableProcessors)+" ")])])],1),a("el-divider"),a("el-row",[a("el-col",{attrs:{span:4}},[a("div",{staticClass:"grid-content bg-purple"},[t._v("物理内存")])]),a("el-col",{attrs:{span:20}},[a("div",{staticClass:"grid-content bg-purple-light item-content"},[a("div",[t._v(" 总物理内存:"+t._s(t.data.totalPhysicalMemorySize)+" MB ")]),a("div",[t._v(" 空闲物理内存:"+t._s(t.data.freePhysicalMemorySize)+" MB ")]),a("div",[t._v(" 总交换空间:"+t._s(t.data.totalSwapSpaceSize)+" MB ")]),a("div",[t._v(" 空闲交换空间:"+t._s(t.data.freeSwapSpaceSize)+" MB ")])])])],1),a("el-divider"),a("el-row",[a("el-col",{attrs:{span:4}},[a("div",{staticClass:"grid-content bg-purple"},[t._v("JVM参数")])]),a("el-col",{attrs:{span:20}},t._l(t.data.vmArgs,(function(e){return a("div",{key:e,staticClass:"grid-content bg-purple-light item-content"},[t._v(" "+t._s(e)+" ")])})),0)],1),a("el-divider"),a("el-row",[a("el-col",{attrs:{span:4}},[a("div",{staticClass:"grid-content bg-purple"},[t._v("类路径")])]),a("el-col",{attrs:{span:20}},t._l(t.data.classPaths,(function(e){return a("div",{key:e,staticClass:"grid-content bg-purple-light item-content"},[t._v(" "+t._s(e)+" ")])})),0)],1),a("el-divider"),a("el-row",[a("el-col",{attrs:{span:4}},[a("div",{staticClass:"grid-content bg-purple"},[t._v("库路径")])]),a("el-col",{attrs:{span:20}},t._l(t.data.libraryPaths,(function(e){return a("div",{key:e,staticClass:"grid-content bg-purple-light item-content"},[t._v(" "+t._s(e)+" ")])})),0)],1),a("el-divider"),a("el-row",[a("el-col",{attrs:{span:4}},[a("div",{staticClass:"grid-content bg-purple"},[t._v("引导类路径")])]),a("el-col",{attrs:{span:20}},t._l(t.data.bootstrapClassPaths,(function(e){return a("div",{key:e,staticClass:"grid-content bg-purple-light item-content"},[t._v(" "+t._s(e)+" ")])})),0)],1)],1)],1)},y=[],j=a("bc3a"),k={name:"VM",data:function(){return{loading:!0,timer:null,id:null,data:null}},methods:{getVmArgs:function(){var t=Object(o["a"])(regeneratorRuntime.mark((function t(){var e;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,j.get("/api/jvms/".concat(this.id,"/vm"));case 2:e=t.sent,this.data=e.data,this.loading=!1;case 5:case"end":return t.stop()}}),t,this)})));function e(){return t.apply(this,arguments)}return e}()},created:function(){this.id=this.$route.params.id;var t=this;this.timer=setInterval((function(){t.getVmArgs()}),1e3)},beforeDestroy:function(){clearInterval(this.timer)}},x=k,O=(a("8e76"),Object(p["a"])(x,C,y,!1,null,"4fcf292e",null)),M=O.exports,S=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"container-fluid"},[a("p",{staticClass:"head"},[t._v("内存")]),t.loading?a("van-skeleton",{staticClass:"van-skeleton",attrs:{title:"",row:8}}):t._e(),t.loading?t._e():a("div",[a("div",{staticClass:"row"},[a("div",{staticClass:"gc_container"},[a("button",{staticClass:"btn btn-default",on:{click:t.gc}},[t._v("GC")])])]),a("div",{staticClass:"row"},t._l(t.forms,(function(e){return a("div",{key:e.columns[1],staticClass:"col-md-6"},[a("ve-line",{attrs:{data:e,colors:t.colors,series:e.series}})],1)})),0)])],1)},F=[],T=(a("b0c0"),a("2ef0")),R=a.n(T),D=a("c3da"),P=a.n(D),$=a("5a0c"),I=a.n($),V=a("bc3a"),B={components:{VeLine:P.a},name:"Memory",data:function(){return{loading:!0,colors:["#304ffe","#b71c1c"],id:null,timer:null,name2Form:{},forms:[]}},methods:{getMemory:function(){var t=Object(o["a"])(regeneratorRuntime.mark((function t(){var e,a,s=this;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,V.get("/api/jvms/".concat(this.id,"/memory"));case 2:e=t.sent,this.loading=!1,a=I()().format("mm:ss"),R.a.each(e.data,(function(t){if(R.a.isEmpty(s.name2Form[t.name])){var e=I()().subtract(30,"second");s.name2Form[t.name]={columns:["time",t.name,"max"],rows:[],series:[{symbol:"none",type:"line",smooth:!1,name:t.name,data:[]},{symbol:"none",type:"line",smooth:!1,name:"max",data:[]}]};for(var n=s.name2Form[t.name],r=0;r<30;r++){var i=e.add(1,"second");n.rows.push({time:i.format("mm:ss")}),n.series[0].data.push(0),n.series[1].data.push(0),e=i}s.forms.push(s.name2Form[t.name])}var l=s.name2Form[t.name];l.rows.shift(),l.series[0].data.shift(),l.series[0].data.push(t.used/1e3/1e3),l.series[1].data.shift(),l.series[1].data.push(t.max/1e3/1e3);var o={time:a};l.rows.push(o)}));case 6:case"end":return t.stop()}}),t,this)})));function e(){return t.apply(this,arguments)}return e}(),gc:function(){var t=Object(o["a"])(regeneratorRuntime.mark((function t(){return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,V.post("/api/jvms/".concat(this.id,"/gc"));case 2:case"end":return t.stop()}}),t,this)})));function e(){return t.apply(this,arguments)}return e}()},created:function(){var t=this;this.id=this.$route.params.id,this.timer=setInterval((function(){t.getMemory()}),1e3)},destroyed:function(){clearInterval(this.timer)}},E=B,L=(a("1439"),Object(p["a"])(E,S,F,!1,null,"ee045120",null)),N=L.exports,z=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"container-fluid"},[a("p",{staticClass:"head"},[t._v("线程")]),a("div",{staticClass:"row"},[t.loading?a("van-skeleton",{staticClass:"van-skeleton",attrs:{title:"",row:8}}):t._e(),t.loading?t._e():a("ve-line",{attrs:{data:t.form,colors:t.colors,series:t.series}})],1),a("div",{staticClass:"row",staticStyle:{"margin-bottom":"14px"}},[a("div",{staticClass:"col-md-6"},[a("button",{staticClass:"btn btn-default",on:{click:t.getThreads}},[a("span",{staticClass:"glyphicon glyphicon-refresh",attrs:{"aria-hidden":"true"}}),t._v("刷新 ")])]),a("div",{staticClass:"col-md-6"},[a("button",{staticClass:"btn btn-default",on:{click:t.getDeadThreads}},[a("span",{staticClass:"glyphicon glyphicon-lock",attrs:{"aria-hidden":"true"}}),t._v("检测死锁 ")])])]),a("div",{staticClass:"row"},[a("div",{staticClass:"col-md-6"},[a("table",{staticClass:"table table-hover table-bordered"},[t._m(0),a("tbody",t._l(t.threads,(function(e){return a("tr",{key:e.id,staticClass:"item",on:{click:function(a){return t.getThreadDetail(e.id)}}},[a("td",[t._v(t._s(e.name))]),a("td",[t._v(t._s(e.state))])])})),0)])]),a("div",{staticClass:"col-md-6",staticStyle:{overflow:"scroll"}},[a("table",{staticClass:"table table-hover"},[a("tbody",[a("tr",[a("td",[a("pre",[t._v(t._s(t.threadDetail))])])])])])])]),a("el-dialog",{attrs:{title:"死锁",visible:t.dialogShow,width:"80%"},on:{"update:visible":function(e){t.dialogShow=e}}},[a("div",{staticClass:"row"},[a("div",{staticClass:"col-md-2"},[a("el-tree",{attrs:{data:t.deadLocks,props:t.defaultProps},on:{"node-click":t.showDeadLock}})],1),a("div",{staticClass:"col-md-8"},[a("pre",[t._v(t._s(t.deadLockThreadDetail))])])])])],1)},J=[function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("thead",[a("tr",[a("th",[t._v("线程名")]),a("th",[t._v("状态")])])])}],A=(a("99af"),a("d81d"),a("bc3a")),G={name:"Thread",components:{VeLine:P.a},data:function(){return{defaultProps:{children:"children",label:"label"},deadLocks:null,dialogShow:!1,loading:!0,id:null,timer:null,threads:[],threadDetail:null,deadLockThreadDetail:null,colors:["#304ffe","#b71c1c"],form:{columns:["time","活动线程","峰值线程"],rows:[]},series:[{symbol:"none",type:"line",smooth:!1,name:"活动线程",data:[]},{symbol:"none",type:"line",smooth:!1,name:"峰值线程",data:[]}]}},methods:{getThreadsSummary:function(){var t=Object(o["a"])(regeneratorRuntime.mark((function t(){var e;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,A.get("/api/jvms/".concat(this.id,"/threads_summary"));case 2:e=t.sent,this.loading=!1,R.a.size(this.form.rows)>=30&&(this.form.rows.shift(),this.series[0].data.shift(),this.series[1].data.shift()),this.form.rows.push({time:I()().format("mm:ss")}),this.series[0].data.push(e.data.activeThreadCount),this.series[1].data.push(e.data.peakThreadCount);case 8:case"end":return t.stop()}}),t,this)})));function e(){return t.apply(this,arguments)}return e}(),getThreads:function(){var t=Object(o["a"])(regeneratorRuntime.mark((function t(){var e;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,A.get("/api/jvms/".concat(this.id,"/threads"));case 2:e=t.sent,this.threads=e.data;case 4:case"end":return t.stop()}}),t,this)})));function e(){return t.apply(this,arguments)}return e}(),getThreadDetail:function(){var t=Object(o["a"])(regeneratorRuntime.mark((function t(e){var a;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,A.get("/api/jvms/".concat(this.id,"/threads/").concat(e));case 2:a=t.sent,this.threadDetail=a.data;case 4:case"end":return t.stop()}}),t,this)})));function e(e){return t.apply(this,arguments)}return e}(),getDeadThreads:function(){var t=Object(o["a"])(regeneratorRuntime.mark((function t(){var e;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return this.deadLockThreadDetail=null,t.next=3,A.get("/api/jvms/".concat(this.id,"/deadlock"));case 3:if(e=t.sent,0!==R.a.size(e.data)){t.next=7;break}return this.$message({message:"没有死锁线程",type:"success"}),t.abrupt("return");case 7:this.deadLocks=R.a.map(e.data,(function(t,e){var a=R.a.map(t,(function(t){return{label:t.name,threadId:t.threadId}}));return{label:"死锁".concat(e),children:a}})),this.dialogShow=!0;case 9:case"end":return t.stop()}}),t,this)})));function e(){return t.apply(this,arguments)}return e}(),showDeadLock:function(){var t=Object(o["a"])(regeneratorRuntime.mark((function t(e){var a;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:if(console.log(e.threadId),R.a.isNumber(e.threadId)){t.next=3;break}return t.abrupt("return");case 3:return t.next=5,A.get("/api/jvms/".concat(this.id,"/threads/").concat(e.threadId));case 5:a=t.sent,this.deadLockThreadDetail=a.data;case 7:case"end":return t.stop()}}),t,this)})));function e(e){return t.apply(this,arguments)}return e}()},created:function(){var t=this;this.id=this.$route.params.id,this.getThreads(),this.timer=setInterval((function(){t.getThreadsSummary()}),1e3)},destroyed:function(){clearInterval(this.timer)}},H=G,U=(a("b1fe"),Object(p["a"])(H,z,J,!1,null,"2fdb57ee",null)),q=U.exports,K=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"content"},[a("p",{staticClass:"head"},[t._v("堆对象统计")]),a("el-row",{attrs:{type:"flex",align:"middle"}},[a("el-col",{attrs:{span:6}},[a("el-input",{attrs:{placeholder:"ClassName"},model:{value:t.classFilter,callback:function(e){t.classFilter=e},expression:"classFilter"}})],1),a("el-col",{attrs:{offset:10,span:4}},[a("span",[t._v("总对象数:"+t._s(t.totalCount))])]),a("el-col",{attrs:{span:4}},[a("span",{},[t._v("总字节数:"+t._s(t.totalBytes))])])],1),a("el-row",[a("el-col",{attrs:{span:24}},[a("el-table",{staticStyle:{width:"100%"},attrs:{data:t.afterFilterObjects}},[a("el-table-column",{attrs:{prop:"className",label:"日期"}}),a("el-table-column",{attrs:{prop:"count",sortable:"",label:"数量",width:"120"}}),a("el-table-column",{attrs:{prop:"bytes",sortable:"",label:"bytes",width:"120"}})],1)],1)],1)],1)},Q=[],W=(a("4de4"),a("ac1f"),a("466d"),a("bc3a")),X={name:"HeapObjects",data:function(){return{classFilter:"",id:null,totalCount:null,totalBytes:null,afterFilterObjects:null,objects:null}},methods:{getObjects:function(){var t=Object(o["a"])(regeneratorRuntime.mark((function t(){var e,a=this;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,W.get("/api/jvms/".concat(this.id,"/objects"));case 2:e=t.sent,this.totalCount=e.data.totalCount,this.totalBytes=e.data.totalBytes,this.objects=e.data.beans,this.afterFilterObjects=this.objects.filter((function(t){return t.className.match(a.classFilter)}));case 7:case"end":return t.stop()}}),t,this)})));function e(){return t.apply(this,arguments)}return e}()},watch:{classFilter:function(t){this.afterFilterObjects=this.objects.filter((function(e){return e.className.match(t)}))}},created:function(){this.id=this.$route.params.id,this.getObjects()}},Y=X,Z=(a("a89b"),Object(p["a"])(Y,K,Q,!1,null,"11313e60",null)),tt=Z.exports,et=[{path:"/jvm/:id",component:w,children:[{path:"vm",component:M},{path:"memory",component:N},{path:"thread",component:q},{path:"objects",component:tt}]},{path:"/*",component:m}],at=new r["a"]({routes:et}),st=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",[a("div",{staticClass:"navbar navbar-default topnav"},[a("div",{staticClass:"container"},[a("div",{staticClass:"navbar-header"},[a("router-link",{staticClass:"navbar-brand",attrs:{to:"/"}},[t._v(" Doctor ")])],1)])]),a("div",{staticClass:"container content"},[a("router-view")],1)])},nt=[],rt={name:"App"},it=rt,lt=(a("5c0b"),Object(p["a"])(it,st,nt,!1,null,null,null)),ot=lt.exports,ct=a("5c96"),dt=a.n(ct);a("0fae");s["default"].use(r["a"]),s["default"].use(n["a"]),s["default"].use(dt.a),s["default"].config.productionTip=!1,new s["default"]({render:function(t){return t(ot)},router:at}).$mount("#app")},"5c0b":function(t,e,a){"use strict";var s=a("9c0c"),n=a.n(s);n.a},"88f3":function(t,e,a){},"8e76":function(t,e,a){"use strict";var s=a("e49f"),n=a.n(s);n.a},"97d9":function(t,e,a){"use strict";var s=a("88f3"),n=a.n(s);n.a},"9c0c":function(t,e,a){},a89b:function(t,e,a){"use strict";var s=a("4403"),n=a.n(s);n.a},b1fe:function(t,e,a){"use strict";var s=a("e36e"),n=a.n(s);n.a},b278:function(t,e,a){},bc98:function(t,e,a){"use strict";var s=a("1d88"),n=a.n(s);n.a},e36e:function(t,e,a){},e49f:function(t,e,a){}});
//# sourceMappingURL=app.b2cc9de4.js.map