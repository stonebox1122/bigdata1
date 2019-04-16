<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bootstrap 101 Template</title>

    <link href="/bootstrap-3.3.7-dist/css/bootstrap.css" rel="stylesheet">
    <script src="/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>
    <script src="/jquery/echarts.min.js"></script>
    <script src="/jquery/jquery-1.12.4.min.js"></script>
</head>
<body>

<div id="main" style="width: 600px;height:400px;"></div>
<script type="text/javascript">
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.getElementById('main'));

    // 指定图表的配置项和数据
    var option = {
        title: {
            text: '用户通话信息统计'
        },
        tooltip: {},
        legend: {
            data:['通话时长']
        },
        xAxis: {
            data: [
                <c:forEach items="${calllogs}" var="calllog">
                    ${calllog.dateid},
                </c:forEach>
            ]
        },
        yAxis: {},
        series: [{
            name: '通话时长',
            type: 'bar',
            data: [
                <c:forEach items="${calllogs}" var="calllog">
                    ${calllog.sumDuration},
                </c:forEach>
            ]
        }]
    };

    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
</script>
    ${calllogs}
</body>
</html>
