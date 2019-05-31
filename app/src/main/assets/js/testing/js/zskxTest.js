//modified in 20140516
(function($) {
    $.extend({
        zskxTest: function(pss, saveKey, u, condition) {
            var json = pss.question;
            var key = saveKey + '_test_answer';
            var q = $.extend(true, {}, json);
            var answer = []; //答案
            var i = 0; //当前下标
            var qs = json.questions;
            var user = u; //{"age":26,"gender":"FEMALE","id":126,"name":"G26"};
            if (!user) {
                uesr = {};
            }
            var beginIndex = -1;
            var endIndex = -1;
            var qNums = [];

            // 9047
            var param = null;
            var pkey = saveKey + '_param';

            // 9045
            var num = 0, // 题目计数
                qIndex = null; // 题目索引
            var qNumber = {}; // 存储跳题时的题目索引{1:2, 2:0, 3:5,...}
            var isNext = true,
                canSubmit = false,
                first = true;
            var begin = true,
                end = false;

            if (condition) {
                q.misfit = eval('(function(user){' + condition + '})(u);');
            }



            /**
             * parse the variable question.autoFillAnswer
             */
            var parseQuestion = function(_question) {
                var autoFillAnswer = _question.autoFillAnswer;
                autoFillAnswer = autoFillAnswer && typeof autoFillAnswer === "string" && JSON.parse(autoFillAnswer) || autoFillAnswer;
                _question.autoFillAnswer_key = autoFillAnswer && autoFillAnswer.key || "";
                _question.autoFillAnswer_value = autoFillAnswer && autoFillAnswer.value && JSON.stringify(autoFillAnswer.value) || "";
                return _question
            }

            var getQ = function(i) {

                try {
                    var value = json.questions[i];
                    value = parseQuestion(value);
                } catch (e) {
                }

                if (qIndex !== null) { // 跳题标识
                    value = parseQuestion(json.questions[qIndex]);
                }

                //执行beforeScript
                exeScript(value.beforeScript, value);
                if (value.commChoice) {
                    var temp = json.commChoices[value.commChoice];
                    //value.type = temp.type;
                    value = $.extend({}, value, temp);
                    delete value.commChoice;
                }
                //数轴特殊一些
                if (value.type == "NUMBERAXIS" && !value.count) {
                    var temp = "";
                    var count = 0;
                    for (var key in value.choiceGroup) {
                        var cs = value.choiceGroup[key];
                        for (var i in cs.choices) {
                            temp = cs.choices[i].title;
                            if (count == 0) {
                                value.minDesc = temp;
                            }
                            count++;
                        }
                    }
                    value.maxDesc = temp;
                    value.count = count;
                }

                return value;
            };

            var exeScript = function(script, $this) { //目的是能取到User
                if (!script) {
                    return true;
                }
                if (script.search('return') == -1) {
                    script = 'return ' + script;
                }
                if (!$this) {
                    $this = {};
                }
                return eval('(function(){' + script + '}).apply($this)');
            };

            (function() {
                for (var i = 0; i < qs.length; i++) {
                    var t = qs[i];
                    var skip = exeScript(t.beforeScript);
                    if (skip !== false) {
                        endIndex = i;
                        qNums.push(i);
                        if (beginIndex < 0) {
                            beginIndex = i;
                        }
                    }
                }
            })();

            var save = function() {
                var temp = null;
                if (answer && answer.length) {
                    temp = JSON.stringify(answer);
                }
                //保存
                if (window.localStorage) { //支持 localStorage
                    if (temp) {
                        localStorage.setItem(key, temp);
                    } else {
                        localStorage.removeItem(key);
                    }
                } else {
                    //只能用cookie了
                    $.cookie(key, temp);
                }
            };

            var saveParam = function() {
                var temp = null;
                if (param != null) {
                    temp = param;
                }
                if (window.localStorage) { // 支持 localStorage
                    if (temp) {
                        localStorage.setItem(pkey, temp);
                    } else {
                        localStorage.removeItem(pkey);
                    }
                } else {
                    $.cookie(pkey, temp);
                }
            }

            var readParam = function() {
                var retVal = null;
                if (window.localStorage) {
                    retVal = localStorage.getItem(pkey);
                } else {
                    retVal = $.cookie(pkey);
                }
                return retVal;
            }
            param = readParam();

            q.setUser = function(u) {
                user = u;
            };

            /**
             * 移除答案
             */
            q.removeAnswer = function() {
                answer = [];
                save();
            };

            /**
             * 开始测试
             */
            q.beginTest = function() {
                answer = [];
                if (param == 'mother') {
                    i = 1;
                } else {
                    i = 0;
                }
                num = 0;
                save();
                return getQ(qNums[i]);
            };

            /**
             * 继续测试
             */
            q.continueTest = function() {
                var index = answer.length - 1;
                var j = 0;
                for (; j < qNums.length; j++) {
                    if (index == qNums[j]) {
                        i = j;
                        break;
                    }
                }
                return getQ(qNums[i]);
            };

            /**
             * 是否完成测试
             */
            q.isFinsh = function() {
                //判断最后一题是否有答案
                return (i == qNums.length - 1) && answer[qNums[i]] && answer[qNums[i]].length > 0;
            };

            /**
             * 返回true表示可以进行下一题测试
             */
            q.isContinue = function() {
                return isNext;
            };

            q.canSubmit = function() {
                return canSubmit;
            }
            /**
             * 取得测试总题数
             */
            q.count = function() {
                if (param == "father" || param == "mother") {
                    return qNums.length / 2;
                }else {
                    return qNums.length;
                }
            };
            /**
             * 取得下一题
             * 如果上一题未答, 返回 undifiend
             */
            q.nextQuestion = function() {
                var temp = qNums[i];
                if (qIndex != null) {
                    temp = qNums[qIndex];
                }
                if (answer[temp]) {
                        if (param == "father" || param == "mother") {
                            i = i + 2;
                        } else {
                            i++;
                        }
                        temp = qNums[i];
                        if (qIndex != null) {
                            temp = qNums[qIndex++];
                        }
                        return getQ(temp);
                }
            };
            /**
             * 保存答案
             * 参数必须为数组,如[A]
             */
            q.answer = function(a) {
                if (qIndex != null) { // 9045 跳题特殊处理
                    answer[qNums[qIndex]] = a;
                } else {
                    answer[qNums[i]] = a;
                }
                save();
            };

            /**
             * 取得答案
             */
            q.getAnswer = function() {
                if (qIndex != null) {
                    return answer[qNums[qIndex]];
                }
                return answer[qNums[i]];
            };

            /**
             * 取出所有答案
             */
            q.allAnswer = function() {
                return $.extend(true, [], answer); //复制一份,以防页面损坏
            };

            /**
             * 取得答案字符串
             */
            q.allAnswerStr = function() {
                return JSON.stringify(answer); //复制一份,以防页面损坏
            };

            /**
             * 从本地读取得答案
             */
            q.readAnswerLocal = function() {
                var string2Json = function(str) {
                    return eval('(' + str + ')');
                };
                var retVal = null;
                if (window.localStorage) { //支持 localStorage
                    retVal = localStorage.getItem(key);
                } else {
                    retVal = $.cookie(key);
                }
                if (retVal == null) {
                    return [];
                }
                return string2Json(retVal);
            };
            answer = q.readAnswerLocal(); //重新获取答案

            /**
             * 取得上一题
             */
            q.upQuestion = function() {
                var temp = qNums[i];
                if (temp > 0) {
                    if (param == "father" || param == "mother") {
                        i = i - 2;
                    } else {
                        i--;
                    }
                    temp = qNums[i];
                    if (qIndex != null) {
                        temp = qNums[qIndex--];
                    }
                    return getQ(temp);
                }
            };

            /**
             * 取得测试题的类型
             */
            q.nowType = function() {
                return qs[qNums[i]].type;
            };
            /**
             * 取得测试题索引
             */
            q.nowIndex = function() {
                var index;
                if (param == "father" || param == "mother") {
                    index = parseInt(i / 2);
                } else {
                    index = i;
                }
                return index;
            };
            /**
             * 开始个数
             */
            q.upCount = function() {
                var ucount;
                if (param == "mother") {
                    ucount = i - 1;
                } else {
                    ucount = i;
                }
                return ucount;
            };

            q.questionIndex = function() {
                return i;
            };

            /**
             * 结束个数
             */
            q.lastCount = function() {
                var lcount;
                if (param == "father") { /*9047 跳题逻辑特殊处理*/
                    lcount = qNums.length - 2 - i;
                } else if (qIndex != null) { /*9045 特殊处理*/
                    lcount = qNums.length - 1 - qIndex;
                } else {
                    lcount = qNums.length - 1 - i;
                }
                return lcount;
            };

            q.getQuestion = function() {
                return getQ(i);
            }

            q.getQIndex = function() {
                return qIndex;
            }

            q.setParam = function(value) {
                param = value;
                saveParam();
            }

            q.getParam = function() {
                return param;
            }

            return q;
        }
    });
})(jQuery);
