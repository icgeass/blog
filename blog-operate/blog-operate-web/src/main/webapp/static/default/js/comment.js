function replyTo(postId, parentId) {
    var quote = '在这里输入你的评论';
    var parentType = '1'; // 文章
    var commentArea = $('#commentArea');
    if (postId != parentId) {
        var item = $('#' + parentId);
        var author = item.find('.author h6').text();
        quote = item.find("div[class='body']").clone().children().remove().end().text().trim();
        parentType = '2'; // 评论
        parentId = parentId.substring(1);
        if (quote.length > 200) {
            quote = quote.substring(0, 200) + "...";
        }
        quote = author + ' said:\n' + quote.replace(/\s*\n\s*/gi, "\n");
    }
    commentArea.find("input[name='postId']").val(postId);
    commentArea.find("input[name='parentId']").val(parentId);
    commentArea.find("input[name='parentType']").val(parentType);
    commentArea.find("textarea[name='content']").attr('placeholder', quote);
}


