/********************
 * @fileName: zskxTestUI.js
 * @version: 1.1
 * @description: 答题脚本
 * 在preview2.html中调用, $('#main').zskxTestUI()
 *
 * @main Function List:
 * 1.submit() 提交答案
 * 2.isContinue() 是否显示‘继续答题’
 * 3.beginTest() 开始答题
 * 4.paddingData() 渲染答题界面
 * 5.toNext() 跳转至下一题
 *
 * @history 修改记录
 * 1.增加2071量表中日期选择功能
 * @history 修改记录
 * 530行修改子题answer.length < 3
 ********************/

(function($) {
    $.fn.zskxTestUI = function(setting) {
        var con = this;
        var ps = $.extend({
            question: null,
            itemId: null,
            customer: null, //{"age": '', "gender": '', "edu": ''},
            guideTmpl: "#guide-tmpl", //简介模板
            testTmpl: "#test-tmpl", //测试
            endTmpl: "#end-tmpl", //结束
            singleTmpl: "#single-tmpl", //单选
            numberaxisTmpl: "#numberaxis-tmpl", //数轴
            ravenTmpl: "#raven-tmpl", //瑞文
            childTmpl: "#child-tmpl", //子题
            onNext: function(current, total) {}, //下一题
            onSubmit: function(param) {}, //questionId, version, itemId, answer
            onBeginTest: function() {},
            onCloseTest: function() {}
        }, setting);

        for (var i in ps) {
            var v = ps[i];
            if ((i + "").indexOf('Tmpl') != -1) {
                ps[i] = (typeof v == 'string' ? $(v) : v);
            }
        }

        var context = {};
        var submitCount = 0;

        /**
         * submit 提交答案
         */
        var submit = function() {
            submitCount++;
            con.find('[data-test=btnDown]').html('正在提交...');
            if (submitCount == 1) {
                //TODO 提交....
                var param = {
                    questionId: ps.question.id,
                    version: ps.question.version,
                    itemId: ps.itemId,
                    answer: t.allAnswerStr()
                };
                ps.onSubmit(param, function(json) {
                    //结束页
                    ps.endTmpl.tmpl(json).appendTo(con.empty());
                }, function() {
                    t.removeAnswer(); //删除答案
                });
            } else if (submitCount > 3) {
                alert('不用那么费劲啦，点击一下就够了:)');
            }
        };

        //题目信息
        var t = $.zskxTest(ps.question, ps.itemId, ps.customer, ps.question.condition);
        (function() {
            var qinfo = $.extend({}, ps.question);
            if (t.misfit) {
                var gender = {
                    MALE: '男',
                    FEMALE: '女'
                };
                qinfo.misfit = t.misfit;
                var temp = $.extend({}, ps.customer);
                temp.gender = gender[temp.gender];
                var edus = {
                    "UNDERPRIMARY": "小学以下",
                    "PRIMARY": "小学",
                    "JUNIOR": "中学",
                    "SENIOR": "高中",
                    "SECONDARY": "中专",
                    "COLLEGE": "大专",
                    "UNDERGRADUATE": "本科",
                    "MASTER": "硕士",
                    "DOCTOR": "博士",
                    "POSTDOCTORAL": "博士后"
                };
                temp.edu = edus[temp.edu];
                qinfo.customer = temp;
            }
            // 渲染指导语页面
            ps.guideTmpl.tmpl(qinfo).appendTo(con.empty());
        })();

        /**
         * isContinue 是否显示‘继续答题’
         */
        var isContinue = function() {
            if (t.allAnswer().length) {
                con.find('[data-test=continueQuestion]').show();
            } else {
                con.find('[data-test=continueQuestion]').hide();
            }

            // 9047量表特殊逻辑使用
            if(ps.question.id == 9047){
                var v=t.getParam();
                $(".category_item :radio[value="+v+"]").attr("checked","checked");
                con.delegate(":radio","change",function(){
                    var val=$(this).val();
                    if(val != v){
                        con.find('[data-test=continueQuestion]').hide();
                    }else{
                        con.find('[data-test=continueQuestion]').show();
                    }
                });
            }
        };
        isContinue();

        /**
         * beginTest 开始答题，并渲染答题界面
         * @param  {Boolean} isContinue 是否继续答题
         */
        var beginTest = function(isContinue) {
            if (t.misfit) {
                alert(t.misfit);
                return;
            }
            //TODO 开始测试回调
            ps.onBeginTest();
            con.empty();
            ps.testTmpl.tmpl({
                count: t.count()
            }).appendTo(con);
            ps.testPanel = con.find('[data-test=test_panel]');
            ps.btnUp = con.find('[data-test=btnUp]');
            ps.btnDown = con.find('[data-test=btnDown]');
            ps.progressNow = con.find('[data-test=progressNow]');
            ps.progressPercent = con.find('[data-test=progressPercent]');
            if (isContinue) { // 为true则从上一题继续答题
                paddingData(t.continueTest());
            } else { // 否则重新开始答题
                paddingData(t.beginTest());
            }
            location.hash = "#continue";
        };

        /**
         * paddingData 渲染答题界面
         * @param  {Object} json 题目信息
         */
        var paddingData = function(json) {
            //修改进度
            context.nowQuestion = json;
            var index = t.nowIndex() + 1; //当前题
            var total = t.count(); //总题数
            json.id=ps.question.id;
            // 9045量表
            var qIndex=t.getQIndex();
            if(qIndex != null){
                index=qIndex+1;
            }

            ps.onNext(index, total);
            ps.progressNow.css('width', (index / total * 100) + "%"); // 进度条
            ps.progressPercent.html(index + " / " + total);

            var questionType = json.type;
            var minShuzhou = null;
            if (questionType == 'SINGLE' || questionType == 'MORE') { // 单选
                ps.testPanel.empty().append(ps.singleTmpl.tmpl(json));
            } else if (questionType == 'NUMBERAXIS') { // 数轴
                ps.testPanel.empty().append(ps.numberaxisTmpl.tmpl(json));
                var count = json.count;
                var minValue=null,maxValue=null;
                var length=json.choiceGroup.length;
                for(i in json.choiceGroup[0].choices){
                    minValue=i;
                }
                for(i in json.choiceGroup[length-1].choices){
                    maxValue=i
                }
                for (minShuzhou in json.choiceGroup[0].choices) {
                    break;
                }
                minShuzhou = parseInt(minShuzhou + '');
                if (!minShuzhou) {
                    minShuzhou = 0;
                }
                if (minShuzhou == 0) {
                    count = count - 1;
                }
                page = $("[data-choice=NUMBERAXIS]").page({
                    minPage: minShuzhou,
                    nowPage: minShuzhou, //当前页
                    pageSize: count, //显示页码数
                    //totalPage: minShuzhou == 0 ? count + 1 : count,
                    onPageChange: function(page) {
                        window.console && console.log("page: %s.", page);
                        var answer = [];
                        if(page<minValue){
                            page=minValue;
                        }else  if(page>maxValue){
                            page=maxValue;
                        }
                        answer.push(page);
                        t.answer(answer);
                        toNext();
                    }
                });
            } else if (questionType == 'RAVEN') { // 瑞文
                ps.testPanel.empty().append(ps.ravenTmpl.tmpl(json));
            } else if (questionType == 'CHILD') { // 子题
                ps.testPanel.empty().append(ps.childTmpl.tmpl(json));
            }

            //填充答案
            var answer = t.getAnswer();
            if (questionType == 'SINGLE' || questionType == 'MORE' || questionType == 'MUTEX') {
                for (var key in answer) {
                    con.find('a[value=' + answer[key] + ']').addClass('test_selected');
                }
                if(con.find('#wdate').length > 0){
                    con.find('#wdate').val(answer);
                }
            } else if (questionType == 'NUMBERAXIS') {
                if (page) {
                    var temp = minShuzhou;
                    if (answer && answer.length > 0) {
                        temp = answer[0];
                    }
                    page.moveToPage(temp);
                }
            } else if (questionType == 'RAVEN') {
                for (var key in answer) {
                    con.find('[choiceValue=' + answer[key] + ']').addClass('sub_s_select');
                }
            } else if (questionType == 'CHILD') {
                for (var key in answer) {
                    con.find('input[value=' + answer[key] + ']').attr("checked", 'checked');
                }
            }

            if (t.lastCount() == 0 && t.isFinsh()) { // 最后一题并已答
                $('#downButton').html('提交测试');
            };

        };

        var qindex=0;
        /**
         * toNext 跳转至下一题
         * @param  {Object} options
         */
        function toNext(options) {
            //判断是否完成
            if (t.lastCount() != 0) {
                var json = t.nextQuestion(); //下一题
                var isNext=t.isContinue();
                if (json) {
                    if (!options && isNext) {
                        setTimeout(function() {
                            paddingData(json); // 渲染下一题
                        }, 100);
                        return true;
                    } else if (options && options.direct) {
                        paddingData(json);
                        return true;
                    }else if(!isNext){ // 9045量表特殊逻辑使用begin
                        var canSubmit=t.canSubmit();
                        if(canSubmit){
                            return submit();
                        }
                        setTimeout(function(){
                            paddingData(t.getQuestion(t.nowIndex()));
                        },100);
                        return true;
                    } // 9045量表特殊逻辑使用end
                }else if(json===null){
                    submit();
                    return "isSubmit";
                }
                return false;
            }

            // 最后一题后直接提交
            setTimeout(function() {
                //TODO 回调提交
                submit();
            }, 100);

        }

        // 开始答题
        con.delegate('[data-test=startQuestion]', 'click', function() {
            // 2063量表特殊逻辑使用
            if(ps.question.id == 2063){
                 var isSubmit01 ;
                 if (window.localStorage) { //支持 localStorage
                        isSubmit01 = localStorage.getItem("isSubmit");
                        if(isSubmit01!=null){
                            localStorage.removeItem("isSubmit");
                        }
                 } else {
                        isSubmit01 = $.cookie("isSubmit");
                        if(isSubmit01!=null){
                            $.cookie("isSubmit",null);
                        }
                 }
            }

            // 9047量表特殊逻辑使用
            if(ps.question.id == 9047){
                var v=con.find(".category_item :checked").val();
                t.setParam(v);
            }

            //开始答题
            beginTest(false);
        });
        con.delegate('[data-test=continueQuestion]', 'click', function() {
            //继续答题
            beginTest(true);
        });

        var autoFillAnswer = "";
        var autoFillPendding = false
        //答题 (选择题)
        con.delegate('[data-choice=SINGLE]', 'click', function() {
            var group = $(this).attr('group');
            if ($(this).hasClass("test_selected")) {
                if ($(this).siblings('.test_selected').length != 0) {
                    $(this).removeClass('test_selected');
                }
                return;
            }
            $(this).addClass('test_selected').siblings('[group!=' + group + ']').removeClass('test_selected');
            //如果选项组全部选中, 自动进入下一题
            var finshed = true;
            $(this).siblings('[group=' + group + ']').each(function() {
                if (!$(this).hasClass("test_selected")) {
                    finshed = false;
                }
            });

            if (t.lastCount() == 0 && t.isFinsh()) {
                ps.btnDown.html('提交测试');                
            }

            // 存储答案
            var answer = [];
            con.find('.test_selected').each(function() {
                answer.push($(this).attr('value'));
            });
            t.answer(answer);

            autoFillAnswer = autoFillAnswer && autoFillAnswer !== "false" ? autoFillAnswer : $(this).attr("data-autoFillAnswer");
            
            //9057量表特殊使用
            if(t.allAnswer().length == 1 && ps.question.id == 9057){
                autoFillAnswer = '{"1":"A"}'
            }

            if (autoFillAnswer === "false" || autoFillAnswer === "") {
                if (finshed) toNext(); // 跳至下一题
                return;
            }
            if (autoFillPendding) return
            autoFillPendding = true

            if (autoFillAnswer !== "false") {
                autoFillAnswer = JSON.parse(autoFillAnswer)

                $.each(autoFillAnswer, function(key, val) {
                    toNext({
                        direct: true
                    });
                    var $elm = $(".test_panel").find("a[value='" + val + "']")
                    $elm.trigger("click")
                })
                autoFillPendding = false;
                autoFillAnswer = "";
                toNext({
                    direct: true
                });
            } else if (finshed) {
                toNext();
            }

        });

        // 显示图片
        con.delegate("#showPic",'click',function(){
            $(".show_picture_layer").show();
            if($(".pic:visible") == $(".pic").last()){
                $("#closePic").show();
            }
        });
        // 关闭图片
        con.delegate("#closePic",'click',function(){
            //$(".pic").hide();
            $(".show_picture_layer").hide();
        });
        /*
         * 切换图片
         */
        con.delegate("#nextPic",'click',function(){
            var p=$(".pic:visible");
            if(p.next("img").length>0){
                $(".pic").hide();
                p.next().show();
            }
            // '上一张'按钮显示
            if($(".pic:visible").prevAll("img").length>0){
                $("#prevPic").show();
            }
            // '关闭'按钮显示
            if(p.nextAll("img").length==1){
                $("#nextPic").hide();
                $("#closePic").css("display","inline-block");
            }else{
                 $("#closePic").hide();
            }
        });
        con.delegate("#prevPic",'click',function(){
            var p=$(".pic:visible");
            if(p.prev("img").length>0){
                $(".pic").hide();
                p.prev().show();
            }
            if($(".pic:visible").nextAll("img").length>0){
                $("#nextPic").show();
            }
            if(p.prevAll("img").length==1){
                $("#prevPic").hide();
            }
            $("#closePic").hide();
        });
	
	// 选择日期并存至答案[['1990-01-01']]  eg: 2071量表
        con.delegate('#wdate', 'focus', function(event) {
            var inputVal = $(this).val();
            if(inputVal !== ''){
                t.answer([inputVal]);
            }else{
                t.answer();
            }
        });

        



        //瑞文
        con.delegate("[data-choice=RAVEN]", 'click', function() {
            $(this).addClass('sub_s_select').siblings().removeClass('sub_s_select');
            var answer = [];
            answer.push($(this).attr('choiceValue'));
            t.answer(answer);
            toNext();
            return false;
        });

        //子题，例如3011量表
        (function() {
            var p = con;
            // 隐藏子题选项，hide('1A', '1B')
            var hide = function() {
                for (var i = 0; i < arguments.length; i++) {
                    var choice = arguments[i];
                    p.find('[data-out=' + choice + ']').hide();
                }
            };
            // 显示子题选项
            var show = function() {
                for (var i = 0; i < arguments.length; i++) {
                    var choice = arguments[i];
                    p.find('[data-out=' + choice + ']').show();
                }
            };
            // 把选项变为‘不可选’状态
            var disabled = function() {
                for (var i = 0; i < arguments.length; i++) {
                    var choice = arguments[i];
                    p.find('[value=' + choice + ']').attr('disabled', true);
                }
            };
            // 把选项变为‘可选’状态
            var undisabled = function() {
                for (var i = 0; i < arguments.length; i++) {
                    var choice = arguments[i];
                    p.find('[value=' + choice + ']').attr('disabled', false);
                }
            };
            // 把选项设为‘选中’状态，change('5A')
            var change = function() {
                for (var i = 0; i < arguments.length; i++) {
                    var choice = arguments[i];
                    p.find('[value=' + choice + ']').attr("checked", 'checked');
                }
                getAnswer();
            };
            var answer = [];
            // 存储所有子题答案
            var getAnswer = function() {
                answer = [];
                p.find('input[type=radio]:checked').each(function() {
                    answer.push($(this).val());
                });
            };

            // 切换子题选项时执行题目脚本
            p.delegate("input[type=radio]", "change", function() {
                getAnswer();
                var nowAnswer = $(this).val();
                var script = context.nowQuestion.changeAnswer;
                if (script) {
                    retVal = eval('(function(){' + script + '})()');
                    if (retVal == 'NEXT') {
                        t.answer(answer);
                    } else if (retVal == 'OK') {
                        t.answer(answer);
                    }
                    if(answer.length<3 && answer[0]!='1A'){
                        t.answer();
                    }
                }
            });
        })();


        //上一题
        con.delegate('[data-test=btnUp]', 'click', function() {
            k=0;
            if (submitCount > 0) {
                alert('呃...答案已提交至服务器:)');
                return;
            }
            if (t.upCount() == 0) {
                //第一道题
                alert('已经是第一道题了！');
            } else {
                paddingData(t.upQuestion());
                con.find('[data-test=btnDown]').html('下一题');
            }
            return false;
        });
        //下一题
        con.delegate('[data-test=btnDown]', 'click', function() {
            if (t.lastCount() == 0) {
                if (t.isFinsh()) {
                    //完成, 点击提交
                    submit();
                }
            } else {
                if (!toNext()) {
                    if($('#wdate').val() == ''){
                        alert('请选择日期');
                        return;
                    }
                    alert('请选择答案');
                }
            }
        });
        //关闭答题界面
        con.delegate('[data-test=close]', 'click', function() {
            //TODO close call back
            ps.onCloseTest();
            ps.guideTmpl.tmpl(ps.question).appendTo(con.empty());
            isContinue();
        });

        //hash
        var hash = location.hash;
        if (hash == '#restart') {
            beginTest(false);
        } else if (hash == '#continue') {
            beginTest(true);
        } else {
            isContinue();
        }
        return this;
    };

})(jQuery);
