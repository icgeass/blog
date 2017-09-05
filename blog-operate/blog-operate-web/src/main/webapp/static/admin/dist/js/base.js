/**
 * 分页提交
 * @param pageNo
 */
function submitPage(pageNo) {
    var form = $("#pageform");
    var currentPage = $("#currentPage");
    if (form.val() != null && currentPage.val() != null) {
        currentPage.val(pageNo);
        form.submit();
        return;
    }
}

function submitFun(elementId, url) {
    $("#" + elementId).attr("action", url);
    $('#' + elementId).submit();
}

function submitConfirmFun(elementId, url, title) {
    if (!confirm(title)) {
        return;
    }
    submitFun(elementId, url);
}


/**
 * 排序表格
 */
function sortingTable() {
    var sorting = "TABLE.table .sorting,TABLE.table .sorting_ASC,TABLE.table .sorting_DESC";
    $(sorting).unbind("click").bind("click", function () {
        var field = $(this).attr("data-orderField")
        var type = $(this).attr("data-orderFieldType");
        type = type == 'DESC' ? "ASC" : 'DESC';
        var form = $("#pageform");
        if ($("#orderField").val() == null) {
            form.append("<input type='hidden' id='orderField' name='orderField'/>");
        }
        if ($("#orderFieldType").val() == null) {
            form.append("<input type='hidden' id='orderFieldType' name='orderFieldType'/>");
        }
        $("#orderField").val(field);
        $("#orderFieldType").val(type);
        form.submit();
    });
}

function onEnterPageSizeClick() {
    $("#pageSize").keydown(function (e) {
        if (e.keyCode == 13) {
            $("#pageform").submit(); //处理事件
        }
    });
}
