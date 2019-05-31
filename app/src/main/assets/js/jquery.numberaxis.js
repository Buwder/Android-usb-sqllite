//last modified 20130515 

(function($) {
    $.extend($.fn, {
        /*实现一个jquery带滑动条的分页插件*/
        page: function(setting) {
            var ps = $.extend({ //page setting
                renderTo: $(this),
                initPosition: 'min', //初始位置

                leftCssName: "page_left", //左边按钮
                barCssName: 'page_bar', //分页区样式
                rightCssName: "page_right", //右边按钮

                numberCssName: 'page_number', //页码区样式
                nowPageCssName: 'page_now_page', //当前页样式
                hoverPageCssName: 'page_hover_page', //鼠标经过时提示样式

                scrollLineCssName: 'scrollLine', //滚动条样式
                scrollCompletedCssName: 'scrollCompleted', //已完成滚动条样式
                scrollButtonCssName: 'scrollButton', //滚动按钮样式
                nowPage: 10, //当前页
                pageSize: 10, //显示页码数
                totalPage: 100, //总页数
                minPage: -1, //最小页
                maxPage: -1, //最大页
                loop: false, //是否循环
                slideChange: false, //滑块改变时 是否回调
                param: {}, //传递参数
                onPageChange: function() {}
            }, setting);
            if (ps.pageSize > ps.totalPage) {
                ps.pageSize = ps.totalPage;
            }

            if (ps.minPage == -1 || ps.maxPage == -1) {
                //计算minPage和maxPage
                //ps.pageSize = ps.maxPage - ps.minPage + 1;

                if (ps.nowPage + ps.pageSize - 1 > ps.totalPage) {
                    ps.maxPage = ps.totalPage;
                } else {
                    ps.maxPage = ps.nowPage + ps.pageSize - 1;
                }

                /*var diff = parseInt(ps.pageSize/2);
          var tempMin = ps.nowPage - diff;
          var tempMax = ps.nowPage + diff;
          if(tempMin < 1){
            tempMin = 1;
            tempMax = tempMax + diff - ps.nowPage + 1;
          }
          if(tempMax > ps.totalPage){
            tempMin = tempMin - (tempMax - ps.totalPage);
            tempMax = ps.totalPage;
          }else if(ps.pageSize%2==0){//偶数
            tempMax = tempMax - 1;
          }
          //ps.minPage = tempMin;
*/
                /*ps.maxPage = tempMax;
          ps.pageSize = ps.maxPage-ps.minPage+1;*/ //避免页数不足
            } else {
                ps.pageSize = ps.maxPage - ps.minPage;
            }
            //强制将renderTo强制转换成jQuery对象
            ps.renderTo = (typeof ps.renderTo == 'string' ? $(ps.renderTo) : ps.renderTo);

            //渲染UI
            var leftDIV = $("<div>&lt;</div>").attr('class', ps.leftCssName).appendTo(ps.renderTo);
            var barDIV = $("<div></div>").attr('class', ps.barCssName).appendTo(ps.renderTo);
            var rightDIV = $("<div>&gt;</div>").attr('class', ps.rightCssName).appendTo(ps.renderTo);

            var pageNumberDIV = $("<div></div>").attr('class', ps.numberCssName).appendTo(barDIV);
            var nowPageDIV = $("<div></div>").attr('class', ps.nowPageCssName).appendTo(pageNumberDIV);
            var hoverPageDIV = $("<div></div>").attr('class', ps.hoverPageCssName).appendTo(pageNumberDIV);
            hoverPageDIV.hide();

            var scrollLineDIV = $("<div></div>").attr('class', ps.scrollLineCssName).appendTo(barDIV);
            var scrollCompletedDIV = $("<div></div>").attr('class', ps.scrollCompletedCssName).appendTo(scrollLineDIV);
            var scrollButtonDIV = $("<div></div>").attr('class', ps.scrollButtonCssName).appendTo(scrollLineDIV);

            var toPage = ps.nowPage; //
            var callBack = function() {
                /*if(toPage>ps.pageSize){
                return ;
              }
              if(toPage>ps.pageSize){
                return ;
              }
              */
                //if(ps.nowPage != toPage){
                if (toPage < ps.minPage) {
                    toPage = ps.minPage;
                } else if (toPage > ps.maxPage) {
                    toPage = ps.maxPage;
                }
                ps.nowPage = toPage;
                ps.onPageChange(toPage, ps.param);
                //}
            };


            //移动范围
            ps.limited = {
                min: scrollButtonDIV.width() / 2 - 3,
                max: scrollLineDIV.width() - scrollButtonDIV.width() / 2 + 3
            };


            //鼠标放在 滚动条上
            scrollLineDIV.mousemove(function(e) {
                if (ps.isMove) {
                    hoverPageDIV.fadeOut(0);
                    return;
                }
                var width = e.pageX - $(this).offset().left;
                hoverPageDIV.css("left", width);

                //计算页码
                toPage = ps.minPage + parseInt((ps.pageSize) * (width / ps.limited.max));
                if (toPage > ps.maxPage) {
                    toPage = ps.maxPage;
                }

                hoverPageDIV.html(toPage + '');
                hoverPageDIV.fadeIn(0);
            }).mouseout(function() {
                hoverPageDIV.fadeOut(0);
            });

            //点击滚动条
            scrollLineDIV.mousedown(function(e) {
                var width = e.pageX - $(this).offset().left;
                //window.console && console.log('width: %s', width);
                slide.move(width);
                //回调
                callBack();
            });

            /*jQuery拖拽功能*/
            var slide = {
                drag: function(e) {
                    var d = e.data;
                    var width = Math.min(Math.max(e.pageX - d.pageX + d.left, ps.limited.min), ps.limited.max);
                    slide.move(width);
                },

                drop: function(e) {
                    //ps.onChanged(parseInt(slider.css('left'))/ps.limited.max,e);
                    //去除绑定
                    $(document).unbind('mousemove', slide.drag);
                    $(document).unbind('mouseup', slide.drop);
                    ps.isMove = false;
                    //回调
                    callBack();
                },
                move: function(width) {
                    //计算页码
                    if (slide.fresh(width)) {
                        return;
                    }
                    toPage = ps.minPage + parseInt((ps.pageSize) * (width / ps.limited.max));
                    if (toPage > ps.maxPage) {
                        toPage = ps.maxPage;
                    }
                    nowPageDIV.html(toPage + '');
                    if (ps.slideChange) {
                        callBack();
                    }
                },
                moveToPage: function(page) {
                    var width = (page - ps.minPage) * (ps.limited.max / ps.pageSize);
                    if (width < ps.limited.min) {
                        width = ps.limited.min;
                    } else if (width > ps.limited.max) {
                        width = ps.limited.max;
                    }
                    if (slide.fresh(width)) {
                        return;
                    }
                    hoverPageDIV.fadeOut(0);
                    scrollButtonDIV.css("left", width);
                    nowPageDIV.css("left", width);
                    scrollCompletedDIV.css("width", width);
                    nowPageDIV.html(toPage + '');
                },
                fresh: function(width) {
                    if (width < 0 || width > ps.limited.max) {
                        return true;
                    }
                    hoverPageDIV.fadeOut(0);
                    //window.console && console.log('width: %s, max:%s', width, ps.limited.max);
                    scrollButtonDIV.css("left", width);
                    nowPageDIV.css("left", width);
                    scrollCompletedDIV.css("width", width);
                }
            };
            //移动到某一页
            ps.moveToPage = function(page) {
                ps.nowPage = page;
                toPage = page;
                slide.moveToPage(page);
            };

            //初始化进度
            slide.moveToPage(ps.nowPage);

            leftDIV.click(function() {
                if (ps.nowPage - 1 >= ps.minPage) {
                    toPage = ps.nowPage - 1;
                    slide.moveToPage(toPage);
                    callBack();
                } else if (ps.loop) {
                    toPage = ps.maxPage;
                    slide.moveToPage(toPage);
                    callBack();
                }
            });
            rightDIV.click(function() {
                if (ps.nowPage + 1 <= ps.maxPage) {
                    toPage = ps.nowPage + 1;
                    slide.moveToPage(toPage);
                    callBack();
                } else if (ps.loop) {
                    toPage = ps.minPage;
                    slide.moveToPage(toPage);
                    callBack();
                }
            });

            scrollButtonDIV.bind('mousedown', function(e) {
                var d = {
                    left: parseInt(scrollButtonDIV.css('left')),
                    pageX: e.pageX
                };
                e.stopPropagation();
                ps.isMove = true;
                $(document).bind('mousemove', d, slide.drag).bind('mouseup', d, slide.drop);
            });
            return ps;
        }
    });
})(jQuery);
