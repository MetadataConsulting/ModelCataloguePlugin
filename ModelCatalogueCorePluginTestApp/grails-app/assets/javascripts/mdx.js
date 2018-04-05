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
                var html = "<p><strong>" + outputStr(d.name) + "</strong></p>";
                html = html + "<p>" + outputStr(d.description) + "</p>";
                d.ext.values.forEach(function (obj) {
                    html = html + "<strong><em>" + outputStr(obj.key) + "</em></strong>" + " : " + outputStr(obj.value) + "</br>";
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

function outputStr(value) {
    return ( value != null && value !== 'null') ? value : '';
}

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
