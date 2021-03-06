layui.use(['jquery', 'laypage', 'table', 'layer', 'element', 'laydate'], function () {
    var $ = layui.$, laypage = layui.laypage
        , table = layui.table, layer = layui.layer
        , element = layui.element;

    table.render({
        elem: '#doorTable'
        , url: '/doorsjson'
        , page: true
        , cols: [[
            {field: 'Id', title: 'ID', align: "center"}
            , {field: 'Location', title: '位置', align: "center"}
            , {field: 'Status', title: '状态', align: "center"}
            , {field: 'Ip', title: 'ip地址', align: "center"}
            , {fixed: 'right', title: '操作', align: 'center', toolbar: '#toolBar'}
        ]]
    });

    //监听工具条 tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
    table.on('tool(doorTable)', function (obj) {
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值
        if (layEvent === 'del') { //删除
            layer.open({
                content: '<div style="text-align: center">确定要删除该门禁吗？</div>'
                , btn: ['取消', '删除'] //按钮
                , btnAlign: 'c'
                , yes: function () {
                    layer.msg("取消删除");
                }
                , btn2: function () {
                    layer.msg("确认删除");
                    obj.del();
                    $.post("/deldoor",
                        {
                            id:data.Id
                        },
                        function(data,status){
                            layer.msg(data);
                        });
                }
            });
        } else if (layEvent === 'edit') { //编辑;
            layer.open({
                type: 2,
                content: ['/updatedoorview?id=' + data.Id+ '&location=' + data.Location+ '&ip=' + data.Ip+ '&status=' + data.Status, 'no'],
                title: "修改信息",
                shade: 0,
                btn: ['确认', '取消'],
                area: ['500', '580'],
                btnAlign: 'c',
                id: 'first',
                resize: false,
                yes: function (index, layero) {
                    var test = $('#' + layero.find('iframe')[0]['name']).get(0);
                    var doc = test.contentDocument;
                    var id = $("#id", doc).val();
                    var location = $("#location", doc).val();
                    var ip = $("#ip", doc).val();
                    var status = $("#status", doc).val();

                    $.post("/updatedoor",
                        {
                            "Content-Type:text/html;charset":"utf8",
                            id:id,
                            location:location,
                            ip: ip,
                            status:status
                        },
                        function(data,status){
                            // layer.msg(data);
                        });
                    obj.update({
                        Location: location,
                        Ip:ip,
                        Status:status
                    });
                    layer.msg('修改成功'+ip);
                    layer.close(index); //如果设定了yes回调，需进行手工关闭
                }
            })
        }
    });

    $("#add").click(function () {
        layer.open({
            type: 2,
            content: ['/adddoorview', 'no'],
            title: '添加门禁',
            area: ['500', '580'],
            resize: false,
            shade: 0,
            id: "second"
        });
    })

});
