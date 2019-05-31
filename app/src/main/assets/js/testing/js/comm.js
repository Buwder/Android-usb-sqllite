//jQuery.tmpl 设置变量
(function($) {
	var table = {};
	$.extend({
		set:function(key, value){
			table[key] = value;
			return "";
		},
		get:function(key){
			return table[key];
		},
		remove : function(key){
			delete table[key];
			return "";
		},
		removeAll : function (){
			for(var key in table){
				delete table[key];
			}
			return "";
		}
	});
})(jQuery);