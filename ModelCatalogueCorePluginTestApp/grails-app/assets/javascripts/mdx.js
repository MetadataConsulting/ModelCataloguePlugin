//= require jquery-2.2.0.min
//= require bootstrap
//= require_self
$( document ).ready(function() {

    $('*[data-api]').hover(function () {
        var e = $(this);
        e.off('hover');
        $('*[data-api]').not(this).popover('hide');

        var is_visible = e.attr('aria-describedby') || false;

        if (!is_visible) {
            $.getJSON(e.data('api'), function (d) {
                var html = "<p><strong>" + d.name + "</strong></p>";
                html = html + "<p>" + d.description + "</p>";
                d.ext.values.forEach(function (obj) {
                    html = html + "<strong><em>" + obj.key + "</em></strong>" + " : " + obj.value + "</br>";
                });
                e.popover({
                    content: html,
                    html: true,
                    placement: top,
                }).popover('show').mouseleave(function () {
                    $(this).popover('hide')
                });
            });
        }
    });

    $('*[data-api]').mouseleave(function () {
        $(this).popover('hide')
    });
});


function selectAllCheckboxes(attName) {
    var elements = document.getElementsByTagName('input');
    for ( var i = 0; i < elements.length; i++ ) {
        var element = elements[i];
        if ( element.getAttribute("type") === 'checkbox' && element.getAttribute("name") === attName ) {
            element.setAttribute('checked', 'checked');
        }
    }
}
function unselectAllCheckboxes(attName) {
    var elements = document.getElementsByTagName('input');
    for ( var i = 0; i < elements.length; i++ ) {
        var element = elements[i];
        if ( element.getAttribute("type") === 'checkbox' && element.getAttribute("name") === attName ) {
            element.removeAttribute('checked')
        }
    }
}
