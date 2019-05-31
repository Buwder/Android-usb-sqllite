//last modified  20140516
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

        //修改路径
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
            ps.guideTmpl.tmpl(qinfo).appendTo(con.empty());
        })();

        var isContinue = function() {
            if (t.allAnswer().length) {
                con.find('[data-test=continueQuestion]').show();
            } else {
                con.find('[data-test=continueQuestion]').hide();
            }

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

        /** 开始测试　*/
        var beginTest = function(isContinue) {
            if (t.misfit) {
                alert(t.misfit);
                return;
            }
            //TODO 开始测试回调
            ps.onBeginTest();
            con.empty();
            ps.testTmpl.tmpl({
                count: t.count(),title: ps.question.title
            }).appendTo(con);
            ps.testPanel = con.find('[data-test=test_panel]');
            ps.btnUp = con.find('[data-test=btnUp]');
            ps.btnDown = con.find('[data-test=btnDown]');
            ps.progressNow = con.find('[data-test=progressNow]');
            ps.progressPercent = con.find('[data-test=progressPercent]');
            if (isContinue) {
                paddingData(t.continueTest());
            } else {
                paddingData(t.beginTest());
            }
            location.hash = "#continue";
        };

        var paddingData = function(json) {
            //修改进度
            context.nowQuestion = json;
            var index = t.nowIndex() + 1; //当前题
            var total = t.count(); //总题数
            var qIndex=t.getQIndex();
            if(qIndex != null){
                index=qIndex+1;
            }
            ps.onNext(index, total);

            ps.progressNow.css('width', (index / total * 100) + "%");
            ps.progressPercent.html(index + " / " + total);

            var questionType = json.type;
            var minShuzhou = null;
            if (questionType == 'SINGLE' || questionType == 'MORE') {
                ps.testPanel.empty().append(ps.singleTmpl.tmpl(json));
            } else if (questionType == 'NUMBERAXIS') {
                ps.testPanel.empty().append(ps.numberaxisTmpl.tmpl(json));
                var count = json.count;

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
                   // totalPage: minShuzhou == 0 ? count + 1 : count,
                    onPageChange: function(page) {
                        var answer = [];
                        answer.push(page);
                        t.answer(answer);
                        toNext();
                    }
                });
            } else if (questionType == 'RAVEN') {
                ps.testPanel.empty().append(ps.ravenTmpl.tmpl(json));
            } else if (questionType == 'CHILD') {
                ps.testPanel.empty().append(ps.childTmpl.tmpl(json));
            }

            //填充答案
            var answer = t.getAnswer();
            if (questionType == 'SINGLE' || questionType == 'MORE' || questionType == 'MUTEX') {
                for (var key in answer) {
                    con.find('a[value=' + answer[key] + ']').addClass('test_selected');
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

            if (t.lastCount() == 0 && t.isFinsh()) {
                $('#downButton').html('提交测试');
            };
        };

        function toNext(options) {
            //判断是否完成

            if (t.lastCount() != 0) {
                //下一题
                var json = t.nextQuestion();
                var isNext=t.isContinue();
                if (json) {
                    if (!options && isNext) {
                        setTimeout(function() {
                            paddingData(json);
                        }, 100);
                        return true;
                    } else if (options && options.direct) {
                        paddingData(json);
                        return true
                    } else if(!isNext){ // 9045量表特殊逻辑使用begin
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

            setTimeout(function() {
                //TODO 回调提交
                submit();
            }, 100);

        }

        //delegate
        con.delegate('[data-test=startQuestion]', 'click', function() {
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

            var answer = [];
            con.find('.test_selected').each(function() {
                answer.push($(this).attr('value'));
            });
            t.answer(answer);

            autoFillAnswer = autoFillAnswer && autoFillAnswer !== "false" ? autoFillAnswer : $(this).attr("data-autoFillAnswer");

            if (autoFillAnswer === "false" || autoFillAnswer === "") {
                if (finshed) toNext()
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

                    var $elm = $(".question_list").find("a[value='" + val + "']")
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

        //瑞文
        con.delegate("[data-choice=RAVEN]", 'click', function() {
            $(this).addClass('sub_s_select').siblings().removeClass('sub_s_select');
            var answer = [];
            answer.push($(this).attr('choiceValue'));
            t.answer(answer);
            toNext();
            return false;
        });

        //子题
        (function() {
            var p = con;
            var hide = function() {
                for (var i = 0; i < arguments.length; i++) {
                    var choice = arguments[i];
                    p.find('[data-out=' + choice + ']').hide();
                }
            };
            var show = function() {
                for (var i = 0; i < arguments.length; i++) {
                    var choice = arguments[i];
                    p.find('[data-out=' + choice + ']').show();
                }
            };
            var disabled = function() {
                for (var i = 0; i < arguments.length; i++) {
                    var choice = arguments[i];
                    p.find('[value=' + choice + ']').attr('disabled', true);
                }
            };
            var undisabled = function() {
                for (var i = 0; i < arguments.length; i++) {
                    var choice = arguments[i];
                    p.find('[value=' + choice + ']').attr('disabled', false);
                }
            };
            var change = function() {
                for (var i = 0; i < arguments.length; i++) {
                    var choice = arguments[i];
                    p.find('[value=' + choice + ']').attr("checked", 'checked');
                }
                getAnswer();
            };
            var answer = [];
            var getAnswer = function() {
                answer = [];
                p.find('input[type=radio]:checked').each(function() {
                    answer.push($(this).val());
                });
            };

            p.delegate("input[type=radio]", "change", function() {
                getAnswer();
                var nowAnswer = $(this).val();
                if(context.nowQuestion){
                    var script = context.nowQuestion.changeAnswer;
                }
                if (script) {
                    retVal = eval('(function(){' + script + '})()');
                    if (retVal == 'NEXT') {
                        t.answer(answer);
                    } else if (retVal == 'OK') {
                        t.answer(answer);
                    }else{
                        t.answer();
                    }
                }
            });
        })();


        //上一题
        con.delegate('[data-test=btnUp]', 'click', function() {
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
                    alert('请选择答案');
                }
            }
        });
        //关闭
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
