<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bootstrap 101 Template</title>

    <link href="/bootstrap-3.3.7-dist/css/bootstrap.css" rel="stylesheet">
    <script src="/jquery/jquery-1.12.4.min.js"></script>
    <script src="/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>
    <script type="text/javascript">
        $(function () {
            $("#btn1").click(function () {
                window.location.href = "/view?tel=" + $("#tel").val() + "&callTime=" + $("#callTime").val();
            })
        })
    </script>
</head>
<body>

<form>
    <div class="form-group">
        <label for="tel">电话号码</label>
        <input type="text" class="form-control" id="tel" placeholder="请输入电话号码">
    </div>
    <div class="form-group">
        <label for="callTime">查询时间</label>
        <input type="text" class="form-control" id="callTime" placeholder="请输入查询时间">
    </div>
    <button type="button" class="btn btn-default" id="btn1">查询</button>
</form>

</body>
</html>
