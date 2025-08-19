/**
 * Created by alabdullahwi on 5/26/2015.
 */


function greenButtonOn(x) {
    if (dataSource == 'E')
        document.getElementById(x).style.background='url(img/green_bar_button_lit.gif)';
    else
        document.getElementById(x).style.background='url(img/blue_bar_button_lit.gif)';
}

function greenButtonOff(x) {
    if (dataSource == 'E')
        document.getElementById(x).style.background='url(img/green_bar_button.gif)';
    else
        document.getElementById(x).style.background='url(img/blue_bar_button.gif)';
}

var bounceApplyButtons = function () {
    var applyBtns = document.querySelectorAll('.applyButtons');

    for (i=0, len=applyBtns.length; i<len; i++) {
        $target = $(applyBtns[i]);

        if (! $target.hasClass('btn-success')) {
            $target.removeClass('btn-primary');
            $target.addClass('btn-success');
        }

        if (! $target.is(":animated") ){
            $target.effect("bounce", { direction: 'left', times:3 }, 150);
        }
    }
};
