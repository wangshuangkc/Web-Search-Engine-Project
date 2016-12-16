$('.fa').click(function(e){
  var query = $(".search").val();
  var newurl = "http://localhost:25801/search?query=" + query;
  $('.results').append("<h3>" + newurl +"</h3>");
  $.ajax({
        type: 'GET',
        url: newurl,
        success: function(data) {
            $('.results').append("<h3>ok</h3>");
            alert(data);
            $('.results').append("<p>here</p>");
            var posts = JSON.parse(data);
            console.log(posts);
            $.each(posts, function() {
                $('.results').append(this);
            });
        }
    });
});
ok