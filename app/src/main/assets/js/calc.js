/**
 * 计算原始分数
 * @param check 选中项
 * @param script 脚本
 * @returns
 */
function calcScore(checked, script, gene, user, when, answer, highScore){
	//var checked = {'A':'2','B':'1','C':'9'};
	var getArgs = function(args){
		var retVal = {};
		if(args.length==0){
			retVal = checked;
		} else {
			for(var i =0; i<args.length; i++){
				var v = args[i];
				if((typeof v) == 'number'){
					retVal[k] = v;
				}
				if((typeof v) == 'object'){
					for(var k in v){
						if((typeof v[k]) == 'number'){
							retVal[k] = v[k];
						}
					}
				};
			}
		}
		return retVal;
	};
	var sum = function(){
		var args = getArgs(arguments);
		var sum = 0;
		for(var key in args){
			sum = sum + parseFloat(args[key]);
		}
		return sum;
	},
	avg = function(){
		var args = getArgs(arguments);
		if(args.length==0){
			args = checked;
		}
		var sum = 0;
		var count = 0;
		for(var key in args){
			sum = sum + parseFloat(args[key]);
			count ++;
		}
		return sum/count;
	},
	count = function(){
		var args = getArgs(arguments);
		if(args.length==0){
			args = checked;
		}
		var count = 0;
		var key = null;//去除警告
		for(key in args){
			count ++;
		}
		return count;
	},
	min = function(){
		var args = getArgs(arguments);
		if(args.length==0){
			args = checked;
		}
		var min = null;
		for(var key in args){
			var value = parseFloat(args[key]);
			if(min){
				if(min > value){
					min = value;
				}
			}else{
				min = value;
			}
		}
		return min;
	},
	max = function(){
		var args = getArgs(arguments);
		if(args.length==0){
			args = checked;
		}
		var max = null;
		for(var key in args){
			var value = parseFloat(args[key]);
			if(max){
				if(max < value){
					max = value;
				}
			}else{
				max = value;
			}
		}
		return max;
	},
	rate = function(){
		var args=getArgs(arguments);
		if(args.length==0){
			args=checked;
		}
		var sum=0,highSum=0;
		for(var key in args){
			sum+=parseFloat(args[key]);
			highSum+=parseFloat(highScore[key]);
		}
		var result=parseFloat(((sum/highSum)*100).toFixed(2));
		return result+'@';		
	};
	if(1==2){//去除警告
		sum();avg();count();min();max();rate();
	}
	if(!script){
		script = 'return sum();';
	}
	if(script.search('return')==-1){
		script = 'return ' + script;
	}
	//不破坏全局
	var retVal = eval('((function(){' + script + '})())');
	if((typeof retVal) == "number"){
		retVal = parseFloat(retVal.toFixed(2));
	}
	return retVal;
}

function eduLevel(edu){
	var eduArray = ["PRIMARY", "JUNIOR", "SENIOR", "SECONDARY", "COLLEGE", "UNDERGRADUATE", "MASTER", "DOCTOR", "POSTDOCTORAL"];
	
	for(var i=0; i<eduArray.length; i++){
		if(edu == eduArray[i]){
			return i+1;
		}
	}
}

function copy(src){
	var retVal = {};
	for(var i in src){
		retVal[i] = src[i];
	}
	return retVal;
}

function calcQuestionScore(calc, answer, user){
	var getCalc = function(key){
		var temp = calc.question[key].commChoice;
		var retVal = calc.question[key];
		if(temp){
			var t = copy(calc.commChoices[temp]);
			if(retVal.beforeScript){
				t.beforeScript = retVal.beforeScript;
			}
			retVal = t;
		}
		return retVal;
	};
	var getAnswerScore = function(key){
		var t = {};
		var value = answer[key];
		for(var i in value){
			t[value[i]] = getCalc(key).choices[value[i]];
		}
		return t;
	};
	var exeScript = function(script){//目的是能取到User
		if(!script){
			return true;
		}
		if(script.search('return')==-1){
			script = 'return ' + script;
		}
		return eval('((function(){' + script + '})())');
	};
	//计算因子分数
	var scoreArray = [];
	for(var key in calc.question){
		var temp = getCalc(key);
		var canCalc = true;
		if(temp.beforeScript){
			canCalc = exeScript(temp.beforeScript);
		}
		if(canCalc !== false){
			if(!answer[key]){
				scoreArray.push(0);
				try{
					window.console.log("The question index " + key + " no answer.");
				}catch (e) {}
			}else{
				try{
				scoreArray.push(calcScore(getAnswerScore(key), temp.calcScript, null, user));
				}catch (e) {
					throw "The question index " + key + " calcScript error. Script:[" + temp.calcScript + "]";
				}	
			}
		}else{
			scoreArray.push(null);
		}
	}
	return scoreArray;
}

/**
 * 计算因子分
 * @param gene 所有因子
 * @param questionScoreArray 所有条目的成绩
 * @returns {Array}
 *
 * 增加参数answer,questions
 * @param answer 所有题目答案
 * @param questions 所有题目，计算最高分
 */
function calcGeneScore(gene, questionScoreArray, user,answer,questions){
	var highScore=[];
	if(questions){
		for(var i in questions.question){
			var temp=null;
			var q=questions.question[i];
			if(q.commChoice){
				var key=q.commChoice;
				temp=questions.commChoices[key].choices;
			}else{
			            temp=q.choices;	
			}
			var max=0;
			for(var t in temp){
				var value=parseInt(temp[t]);
				if(value>max){
					max=value;
				}
			}
			highScore.push(max);
		}
	}

	var genescoreArray = [];
	/**
	 * 所有因子成绩
	 */
	var genescores = {};
	for(var key in gene){
		var genescore = {};
		var g = gene[key];
		var score = '-';
		var isView=true;
		if(g.questions){
			var array = g.questions.split(',');
			var temp = {};
			for(var k in array){
				var t = parseInt(array[k]);
				var value = questionScoreArray[t];
				if(value !== null){//跳题运算
					temp[t] = value;
				}
			}
			try{
				score = calcScore(temp, g.calcScript, genescores, user, 'calcGeneScore',answer,highScore);
			}catch (e) {
				throw "Gene [" + g.key + "] calcScript Error."; 
			}
		}else if(g.calcScript){
			try{
				score = calcScore(genescores, g.calcScript, genescores, user, 'calcGeneScore',answer,highScore);
			}catch (e) {
				throw "Gene [" + g.key + "] calcScript Error."; 
			}
		}

		/**
		 * 执行量表库中因子判断脚本
		 * @param  temp.calcScript2 [因子判断脚本]
		 */
		if(g.calcScript2){
			try{
				eval('(function(temp){'+g.calcScript2+'}).call(gene,g);');
			}catch (e) {}
		}

		if(g.viewScore){
			isView=true;
		}else{
			isView=false;
		}
		genescores[g.key] = score;
		genescore.key = g.key; 
		genescore.score = score;
		genescore.isView=isView;
		genescoreArray.push(genescore);
	}
	return genescoreArray;
}

function string2Json(str){
	return eval('(' + str + ')');
}

function getAppraise(gene, geneScore, warn, user, answer){
	if(!warn){
		warn = {};
	}
	var geneScoreObj = {};
	for(var i in geneScore){
		var t = geneScore[i];
		geneScoreObj[t.key] = t.score;
	}
	var getScore = function(key){
		return geneScoreObj[key];
	};
	
	/**
	 * 处理评语
	 * @param forceAppraise 是否强制显示评语
	 * @param warn 预警分数，此预警分数可以定制
	 */
	var handelAppraise = function(geneDetail, scoreArray, retVal, forceAppraise){
		//分数＆评语
		if(geneDetail.viewAppraise || forceAppraise){//显示评语
			retVal['content'] = '';
			retVal['suggestion'] = '';
			var w = warn[retVal.key];
			if(!w){
				w = [];
			}
			for(var tempI=0; tempI<scoreArray.length; tempI++){
				var score = scoreArray[tempI];
				for(var i=0; i<w.length; i++){
					var o = w[i];
					if(score>=o.min && score<=o.max){
						retVal.warn = true;
						if(!retVal.warnInfo){
							retVal.warnInfo = [];
						}
						retVal.warnInfo.push(o);
					}
				}
				for(var a in geneDetail.appraise){
					var value = geneDetail.appraise[a];
					if(score>=value.minScore && score<=value.maxScore){
						retVal['content'] += value.content;
						retVal['suggestion'] += value.suggestion;
					}
				}
			}
		}
	};
	var get = function(key, scoreArray){
		var retVal = {};
		for(var i in gene){
			var temp = gene[i];
			if(temp.key == key){
				retVal.key = temp.key;
				retVal.title = temp.title;
				handelAppraise(temp, scoreArray, retVal, true);
			}
		}
		return retVal;
	};
	
	var retArray = [];
	for(var i in gene){
		var temp = gene[i];
		var retVal = {};
		retVal.key = temp.key;
		retVal.title = temp.title;
		/**
		 * 执行因子判断脚本
		 * @param  temp.calcScript2 [因子判断脚本]
		 */
		if(temp.calcScript2){
			try{
			eval('(function(){'+temp.calcScript2+'})();');
			}catch (e) {}
			temp=gene[i];
		}
		
		var scoreArray = getScore(temp.key);

		retVal.ascore=scoreArray;
		if(temp.viewScore){//显示分数
			retVal.score = scoreArray;
		}
		if(typeof scoreArray=="number"){
			scoreArray = [scoreArray];//匹配评语
		}
		
		if(scoreArray instanceof Array){
			handelAppraise(temp, scoreArray, retVal);
		}else{
			//get(因子代码, 分数)
			if(temp.calcScript){
				try{
					eval('(function(gene, appraise, get, when){'+temp.calcScript+'}).call(retVal, geneScoreObj, gene, get, "report");');
				}catch (e) {
				}
			}
		}
		
		retArray.push(retVal);
	}
	return retArray;
}


//java--------------------------------------------
var js2Java = null;
js2Java = function(o){
	if(o instanceof Array){
		var list = new Packages.java.util.ArrayList();
		for(var i=0; i<o.length; i++){
			var t = o[i];
			list.add(js2Java(t));
		}
		return list;
	}
	else if(o instanceof Object){
		var map = new Packages.java.util.LinkedHashMap();
		for(var k in o){
			map.put(k, js2Java(o[k]));
		}
		return map;
	}else{
		return o;
	}
};

/**
 * 取得原始分数
 * @param calc
 * @param answer
 * @param list
 */
function getQuestionScore(calc, answer, list, user){
	if((typeof calc) == 'string'){
		calc = string2Json(calc);
	}
	if((typeof answer) == 'string'){
		answer = string2Json(answer);
	}
	if((typeof user) == 'string'){
		user = string2Json(user);
	}
	var score = calcQuestionScore(calc, answer, user);
	for(var key in score){
		list.add(score[key]);
	}
}

/**
 * 取得因子分
 * @param gene
 * @param questionScore
 * @param list
 *
 * 增加参数answer,questions
 * @param answer 所有题目答案
 * @param questions 所有题目，计算最高分
 */
function getGeneScore(gene, questionScore, list, user,answer,questions){
	if((typeof gene) == 'string'){
		gene = string2Json(gene);
	}
	if((typeof questionScore) == 'string'){
		questionScore = string2Json(questionScore);
	}
	if((typeof user) == 'string'){
		user = string2Json(user);
	}

	if((typeof answer) == 'string'){
		answer = string2Json(answer);
	}
	if((typeof questions) == 'string'){
		questions = string2Json(questions);
	}

	var array = calcGeneScore(gene, questionScore, user,answer,questions);
	for(var key in array){
		var value = array[key];
		var map = new Packages.java.util.LinkedHashMap();
		for(var k in value){
			map.put(k, value[k]);
		}
		list.add(map);
	}
}
function getAppraiseJava(gene, geneScore, list, warn, user, answer){
	if((typeof gene) == 'string'){
		gene = string2Json(gene);
	}
	if((typeof geneScore) == 'string'){
		geneScore = string2Json(geneScore);
	}
	if(warn){
		warn = string2Json(warn);
	}
	if((typeof user) == 'string'){
		user = string2Json(user);
	}
	if((typeof answer) == 'string'){
		answer = string2Json(answer);
	}
	var array = getAppraise(gene, geneScore, warn, user, answer);
	//遍历因子的目的是保证顺序
	for(var i=0; i<array.length; i++){
		var value = array[i];
		list.add(js2Java(value));
	}
}

function getGeneInfoJava(gene, list){
	if((typeof gene) == 'string'){
		gene = string2Json(gene);
	}
	
	//遍历因子的目的是保证顺序
	for(var i=0; i<gene.length; i++){
		var g = gene[i];
//		if(!g.viewScore){
//			continue;
//		}
		var array = g.appraise;
		if((!array) || array.length==0){
			continue;
		}
		var min = 99999;
		var max = -99999;
		for(var k=0; k<array.length; k++){
			var a = array[k];
			if(a.minScore < min){
				min = a.minScore;
			}
			if(a.maxScore > max){
				max = a.maxScore;
			}
		}
		var map = new Packages.java.util.LinkedHashMap();
		map.put("minScore", min);
		map.put("maxScore", max);
		map.put("key", g.key);
		map.put("title", g.title);
		list.add(map);
	};
}


function getGeneNameJava(gene, list){
	if((typeof gene) == 'string'){
		gene = string2Json(gene);
	}
	
	//遍历因子的目的是保证顺序
	for(var i=0; i<gene.length; i++){
		var g = gene[i];
		if(g.viewScore){
			var map = new Packages.java.util.LinkedHashMap();
			map.put("key", g.key);
			map.put("title", g.title);
			var a = new Packages.java.util.ArrayList();
			for(var temp=0; temp<g.appraise.length; temp++){
				var o = g.appraise[temp];
				var x = new Packages.java.util.LinkedHashMap();
				x.put("minScore", o.minScore);
				x.put("maxScore", o.maxScore);
				a.add(x);
			}
			map.put("appraise", a);
			list.add(map);
		}
	};
}



function getAllGeneNameJava(gene, list){
	if((typeof gene) == 'string'){
		gene = string2Json(gene);
	}
	
	//遍历因子的目的是保证顺序
	for(var i=0; i<gene.length; i++){
		var g = gene[i];
		var map = new Packages.java.util.LinkedHashMap();
		map.put("key", g.key);
		map.put("title", g.title);
		var a = new Packages.java.util.ArrayList();
		if(g.appraise){
			for(var temp=0; temp<g.appraise.length; temp++){
				var o = g.appraise[temp];
				var x = new Packages.java.util.LinkedHashMap();
				x.put("minScore", o.minScore);
				x.put("maxScore", o.maxScore);
				a.add(x);
			}
		}
		map.put("appraise", a);
		list.add(map);
	};
}
