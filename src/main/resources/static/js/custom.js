$(document).ready(function(){
    $('.collapsible').collapsible({
        accordion : false // A setting that changes the collapsible behavior to expandable instead of the default accordion style
    });

    $('select').material_select();

    $("body").on("submit","#searchForm",function (e) {
        e.preventDefault();

        $.ajax({
            type: "GET",
            url: $(this).attr('action'),
            data: $(this).serialize(),
            success: function (data) {
                $('#Ads').html(data);
            }
        });
    });
});