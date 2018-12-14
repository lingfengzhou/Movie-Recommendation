$(function () {
    console.log("Just for test.");
    var searchButton = $("#searchButton");
    var searchInput = $("#searchInput");
    var table = $("#movie-table > tbody");
    searchButton.on("click", function () {
        $.ajax({
            url: "/search",
            data: {
                "title": searchInput.val()
            }
        })
            .done(function (data) {
                data = JSON.parse(data);
                var origin = data.origin;
                var related = data.related;
                var movies = [origin].concat(related);
                table.empty();
                for (var i = 0; i < movies.length; i++) {
                    table.append(
                        $("<tr>")
                            .append($("<td>").append(movies[i].title))
                            .append($("<td>").append(movies[i].genres.join(", ")))
                            .append($("<td>").append(movies[i].director))
                            .append($("<td>").append(movies[i].actor))
                            .append($("<td>").append(movies[i].length))
                            .append($("<td>").append(movies[i].year))
                            .append($("<td>").append(movies[i].score))
                    )
                }
            })
            .fail(function () {
                alert("Fail");
            })
    })
});