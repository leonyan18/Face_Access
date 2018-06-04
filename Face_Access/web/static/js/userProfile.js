layui.use(['laydate', 'form', 'layer', 'upload'], function () {
    var laydate = layui.laydate, form = layui.form
        , layer = layui.layer, upload = layui.upload;
    var $ = layui.jquery;

    upload.render({
        elem: '#image' //绑定元素
        , url: '/upload' //上传接口
        , done: function (res) {
            //上传完毕回调
            layer.msg("success");
        }
        , error: function () {
            //请求异常回调
            layer.msg("error");
        }
        , field: 'image'
        , accept: 'images'
        , acceptMime: 'image/*'
        , auto: false
        , bindAction: '#submit'
        , choose: function (obj) {
            obj.preview(function (index, file, result) {
                $("#preview").show();
                $('#img_preview').attr('src', result);
                $("#demo1").attr('src', result);
            });
        }
    });

    $("#demo1").click(function () {
        layer.open({
            type: 1,
            title: false,
            closeBtn: 0,
            // skin: 'layui-layer-nobg', //没有背景色
            shadeClose: true,
            content: $('#div_preview')
        });
    });

});
