/*
 **@version:  v0.0.3.1 20140522
 **@desc:     fix bug:can not show right side xAxis
 **@author:   @unclelongmao
 */
(function(window, $) {

    var reportImage = {};

    (function(window, $) {

        var globalConfig = {
            plotOptions: {
                series: {
                    animation: true
                }
            }
        };

        reportImage.setGlobalConfig = function(g) {
            globalConfig = g;
        };
        reportImage.setAnimation = function(enabled) {
            globalConfig.plotOptions.series.animation = enabled;
        };

        var colors = [
            '#4572A7',
            '#AA4643',
            '#89A54E',
            '#80699B',
            '#3D96AE',
            '#DB843D',
            '#92A8CD',
            '#A47D7C',
            '#B5CA92',

            '#4572A7',
            '#AA4643',
            '#89A54E',
            '#80699B',
            '#3D96AE',
            '#DB843D',
            '#92A8CD',
            '#A47D7C',
            '#B5CA92',

            '#4572A7',
            '#AA4643',
            '#89A54E',
            '#80699B',
            '#3D96AE',
            '#DB843D',
            '#92A8CD',
            '#A47D7C',
            '#B5CA92'
        ];

        /**
         * 合并配置
         * @param imageConfig
         * @param userConfig
         * @return {*}
         */
        var handleConfig = function(imageConfig, userConfig) {

            var comm = {
                /*title: {
                    text: ''
                },*/
                exporting: {
                    enabled: false
                },
                credits: {
                    enabled: false,
                    href: "http://www.zskx.com.cn/",
                    text: '中盛凯新'
                },
                yAxis: {
                    lineWidth: userConfig.yAxis && typeof userConfig.yAxis.lineWidth !== "undefined" ? userConfig.yAxis.lineWidth : 1,
                    gridLineDashStyle: 'longdash'
                },
                colors: colors
            };

            //fix bug: graph 1001
            var graph_yAxis, graph_xAxis;
            if ($.isArray(userConfig.yAxis)) {
                graph_yAxis = userConfig.yAxis
            }

            if ($.isArray(userConfig.xAxis)) {
                graph_xAxis = userConfig.xAxis
            }

            //增加图标题
            if(typeof userConfig.title == "undefined"){
                comm.title={text:''};
            }

            var whole_config = $.extend(true, imageConfig, userConfig, comm, globalConfig);

            if (graph_yAxis && !$.isArray(whole_config.yAxis)) {
                whole_config.yAxis = graph_yAxis
            }

            if (graph_xAxis && !$.isArray(whole_config.xAxis)) {
                whole_config.xAxis = graph_xAxis
            }
            return whole_config;
        };

        /**
         * 雷达图
         * datas = [{name:"第一次测试"},data[12, 12, 13, 14, 15, 16, 27, 18]]
         */
        reportImage.spider = function(userConfig, geneName, datas, context) {
            $(".graph-single").height(300)
            var showPanel = false;
            if (datas.length > 1) {
                showPanel = true;
            }
            var index = 0;
            var label = {};
            var du = 360 / geneName.length;

            var imageConfig = {
                pane: {
                    startAngle: 0,
                    endAngle: 360
                },
                chart: {
                    polar: true,
                    width: 500,
                    height: 250
                },
                xAxis: {
                    tickInterval: du,
                    min: 0,
                    max: 360,
                    labels: {
                        formatter: function() {
                            var temp = label[this.value];
                            if (!temp) {
                                temp = geneName[index];
                                label[this.value] = temp;
                                index++;
                            }
                            return temp;
                        }
                    }
                },
                legend: {
                    enabled: false
                },
                yAxis: {
                    min: 0
                },
                tooltip: {
                    formatter: function() {
                        var str = label[this.x] + ': ' + this.y + '分';
                        if (showPanel) {
                            str = '<b>' + this.series.name + '</b><br/>' + str;
                        }
                        return str;
                    }
                },
                plotOptions: {
                    series: {
                        pointStart: 0,
                        pointInterval: du
                    },
                    column: {
                        pointPadding: 0,
                        groupPadding: 0
                    }
                },
                series: (function(datas) {
                    for (var i in datas) {
                        datas[i].type = 'area';
                    }
                    return datas;
                })(datas)
            };

            return new Highcharts.Chart(handleConfig(imageConfig, userConfig));
        };

        //柱状图
        reportImage.column = function(userConfig, geneName, datas, context) {

            var imageConfig = {
                chart: {
                    type: 'column'
                },
                xAxis: {
                    categories: geneName
                },
                yAxis: {
                    title: {
                        text: '分数'
                    }
                },
                tooltip: {
                    formatter: function() {
                        return this.x + ':' + this.y + '分';
                    }
                },
                legend: {
                    enabled: false
                },
                series: (function(datas) {
                    var d = datas.data;
                    var array = [];
                    for (var i in d) {
                        array.push({
                            y: d[i],
                            color: colors[i]
                        });
                    }
                    return [{
                        name: '',
                        data: array,
                        color: 'white'
                    }];
                })(datas[0])
            };
            /*
             [{
             name: '',
             data: data,
             color: 'white'
             }]

             */
            return new Highcharts.Chart(handleConfig(imageConfig, userConfig));
        };


        //折线图
        reportImage.line = function(userConfig, geneName, datas, context) {
            var isxAxisZero = userConfig.isxAxisZero || false
            var geneName = isxAxisZero ? userConfig.categories : geneName

            var imageConfig = {
                chart: {
                    type: 'line'
                },
                xAxis: {},
                yAxis: {
                    title: {
                        text: '分数'
                    }
                },
                tooltip: {
                    formatter: function() {
                        var t = typeof this.x === "number" ? geneName[this.x] : this.x
                        return t + ':' + this.y + '分';
                    }
                },
                legend: {
                    enabled: false
                },
                series: (function(datas) {
                    var d = datas.data;
                    var array = [];
                    for (var i in d) {
                        array.push({
                            y: d[i],
                            color: colors[0]
                        });
                    }
                    return [{
                        data: array
                    }];
                })(datas[0])
                /**
                series: [{
                    data: [29.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4]
                }]
                 */
            };

            if (!isxAxisZero) {
                imageConfig.xAxis.categories = geneName
            }

            /*
             [{
             name: '',
             data: data,
             color: 'white'
             }]

             */

            return new Highcharts.Chart(handleConfig(imageConfig, userConfig));
        };

        //坐标图
        reportImage.coord = function(userConfig, geneName, datas, context) {
            $(".graph-multi").attr("style", "margin:0;float:left")
            var center = 50;
            var imageConfig = {
                chart: {
                    type: 'scatter'
                },
                xAxis: {
                    //categories: geneName,
                    title: {
                        text: geneName[0]
                    },
                    min: 0,
                    max: 100,
                    plotBands: [{
                        color: '#FF0000',
                        width: 1,
                        value: center,
                        zIndex: 3
                    }, {
                        color: 'rgba(250,143,158, 0.3)',
                        from: 43.3,
                        to: 56.7,
                        value: center,
                        zIndex: 2
                    }, {
                        color: 'rgba(250,143,158, 0.2)',
                        from: 38.5,
                        to: 61.5,
                        value: center,
                        zIndex: 1
                    }]
                },
                yAxis: {
                    title: {
                        text: geneName[1]
                    },
                    min: 0,
                    max: 100,
                    plotBands: [{
                        color: '#FF0000',
                        width: 1,
                        value: center,
                        zIndex: 3
                    }, {
                        color: 'rgba(250,143,158, 0.3)',
                        from: 43.3,
                        to: 56.7,
                        value: center,
                        zIndex: 2
                    }, {
                        color: 'rgba(250,143,158, 0.2)',
                        from: 38.5,
                        to: 61.5,
                        value: center,
                        zIndex: 1
                    }, {
                        width: 1,
                        value: 0,
                        zIndex: 7,
                        label: {
                            text: '外向 稳定',
                            style: {
                                color: 'black',
                                font: 'normal 11px Verdana, sans-serif'
                            },
                            align: 'right',
                            textAlign: 'right',
                            verticalAlign: 'bottom',
                            x: 0
                        }
                    }, {
                        width: 1,
                        value: 0,
                        zIndex: 7,
                        label: {
                            text: '内向 稳定',
                            style: {
                                color: 'black',
                                font: 'normal 11px Verdana, sans-serif'
                            },
                            align: 'left',
                            textAlign: 'left',
                            verticalAlign: 'bottom',
                            x: 0
                        }
                    }, {
                        width: 1,
                        value: 95,
                        zIndex: 7,
                        label: {
                            text: '内向 不稳定',
                            style: {
                                color: 'black',
                                font: 'normal 11px Verdana, sans-serif'
                            },
                            align: 'left',
                            textAlign: 'left',
                            verticalAlign: 'bottom',

                            x: 0
                        }
                    }, {
                        width: 1,
                        value: 95,
                        zIndex: 7,
                        label: {
                            text: '外向 不稳定',
                            style: {
                                color: 'black',
                                font: 'normal 11px Verdana, sans-serif'
                            },
                            align: 'right',
                            textAlign: 'right',
                            verticalAlign: 'bottom',
                            x: 0
                        }
                    }]
                },
                tooltip: {
                    formatter: function() {
                        return geneName[0] + " : " + this.x + '分 <br />' + geneName[1] + " : " + this.y + '分';
                    }
                },
                legend: {
                    enabled: false
                },
                series: (function(datas) {
                    var data = datas.data;

                    return [{
                        name: 'Female',
                        color: colors[0],
                        data: [
                            [data[0], data[1]]
                        ]

                    }];
                })(datas[0])
                /**
                 series: [{
                    data: [29.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4]
                }]
                 */
            };

            return new Highcharts.Chart(handleConfig(imageConfig, userConfig));


        };

        //
        reportImage.gauge = function(userConfig, geneName, datas, context) {
            var center = 50;
            var imageConfig = {
                chart: {
                    type: 'gauge'
                },
                pane: {
                    startAngle: -150,
                    endAngle: 150
                },
                yAxis: {

                },

                series: []
            }

            return new Highcharts.Chart(handleConfig(imageConfig, userConfig));
        };

    })(window, $);


    window.reportImage = reportImage;
    window.showImage = function(gene, script, containerId, title, subtitle) {
        window.json = typeof gene !== "undefined" ? {
            appraise: gene
        } : {};
        var evalScript = function(script, containerId) {
            //只允许访问context和image
            var context = {};
            var getGene = function(key) {
                for (var i = 0; i < gene.length; i++) {
                    var t = gene[i];
                    if (t.key == key) {
                        return t;
                    }
                }
            };
            var image = function(showGene, imageType, config) { //this为默认上下文
                if (!reportImage[imageType]) {
                    if (window.console) {
                        console.log('Has no report image named "' + imageType + '"');
                    }
                    return;
                }

                var geneName = [],
                    data = [];
                for (var i in showGene) {
                    var t = getGene(showGene[i]);
                    if (t) {
                        geneName.push(t.title);
                        data.push(t.score);
                    }
                }
                if (!config) {
                    config = {};
                }
                if (!config.chart) {
                    config.chart = {};
                }
                config.chart.renderTo = containerId;

                return reportImage[imageType](config, geneName, [{
                    name: '测试结果',
                    data: data
                }], context);
            };
            return eval('(function(){var context;' + script + '}).call(context);');
        };
        evalScript(script, containerId);
    };
})(window, jQuery);
