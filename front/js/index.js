var lists = {"video": []};
var replace = "https://www.ted.com/talks/";
var embed = "https://embed.ted.com/talks/lang/zh-cn/";
var settings = "width=\"320\" height=\"180\" frameborder=\"1\" scrolling=\"no\" webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>";
var cnt = 0;

$('.fa').click(function(e){
    var query = $(".search").val();
    $('.results').html("");
    if (query) {
    $('.results').show();
        var newurl = "http://localhost:25801/search?query=" + query;
        $.ajax({
                type: 'GET',
                url: newurl,
                dataType: 'json',
                encode: true
        }).done(function (data) {
                lists = data.video;
                if (Object.keys( lists ).length == 0) {
                    $('.results').append("<h3>No Results Found</h3><br/>");
                } else {
                    $('.results').append("<h3>Total " + Object.keys( lists ).length + " Talks found</h3><br/>");
                    for (var i in lists) {
                        var url = lists[i].url;
                        var newUrl = url.replace(replace, embed);
                        var title = lists[i].title;
                        var link = url + "?language=zh-cn";
                        cnt = (cnt + 1) % 4;
                        if(cnt == 1) {
                            var added = "<tr><td><iframe class=\"preview\" src=\"" + newUrl + "\" " + settings + "<p><a href=\"" + link + "\" target=\"_blank\">" + title + "</a></p></td>"
                            $('.results').append(added);
                        } else {
                            $('.results').append("<td><iframe class=\"preview\" src=\"" + newUrl + "\" " + settings);
                            $('.results').append("<p><a href=\"" + link + "\" target=\"_blank\">" + title + "</a></p></td>");
                        }
                    }
                    if (cnt == 0) {
                        $('.results').append("</tr>");
                    }
                }

        });
    }
});