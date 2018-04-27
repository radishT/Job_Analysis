<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE>
<html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Echarts Demo</title>
<script src="js/echarts.js" ></script>
<script src="js/echarts-wordcloud.js" ></script>
<script type="text/javascript">
	function initData() {
		// 调用后台服务器操作
		// 建立核心对象
		xmlHttp = new XMLHttpRequest();
		// 设置后台进行数据库操作的Servlet地址。此时readyState=1
		xmlHttp.open("get","LoadDataServlet");
		// 设置读取到数据后继续处理的js回调函数
		xmlHttp.onreadystatechange = initDataCallback;
		// 发送数据。此时readyState=2
		xmlHttp.send();
	}
	
	function initDataCallback() {
		// 判断数据是否完全返回了
		if (xmlHttp.readyState == 4) {
			// 判断是否正确返回
			if (xmlHttp.status == 200) {
				// 接收结果
				var result = eval("("+xmlHttp.responseText+")");
				
				// 开始写到图表中。
				// 找到要绘制图表的div
				var myChart = echarts.init(document.getElementById('main'));
				
				var xArray = new Array();
				var dataArray = new Array();
				var data = [];
				// 根据返回的数据来建立数组
				for (var i = 0;i < result.length;i++) {
					data.push({
		                name: result[i].key,
		                value: result[i].count
		            })
				}
				
				var maskImage = new Image();
		        maskImage.src = "images/timg.jpg";

		     // 配置图表的各种属性。
				var option = {
		                tooltip: {},
		                series: [ {
		                    type: 'wordCloud',
		                    gridSize: 2,
		                    sizeRange: [12, 100],
		                    rotationRange: [-90, 90],
		                    shape: 'circle',
		                    width: 1400,
		                    height: 1000,
		                    maskImage:maskImage,
		                    drawOutOfBound: false,
		                    textStyle: {
		                        normal: {
		                            color: function () {
		return 'rgb(' + [
		                                    Math.round(Math.random() * 160),
		                                    Math.round(Math.random() * 160),
		                                    Math.round(Math.random() * 160)
		                                ].join(',') + ')';
		                            }
		                        },
		                        emphasis: {
		                            shadowBlur: 10,
		                            shadowColor: '#333'
		                        }
		                    },
		                    data:data
		                } ]
		            };

				maskImage.onload = function(){
					option.series[0].maskImage = maskImage;
					myChart.setOption(option);
				};
				// 使用刚指定的配置项和数据显示图表。
				myChart.setOption(option);
			}
		}
	}

	
</script>
</head>
<body onload="initData();" >
	<div id="main" style="width:1400px;height:1000px;border:1px solid" ></div>
</body>
</html>