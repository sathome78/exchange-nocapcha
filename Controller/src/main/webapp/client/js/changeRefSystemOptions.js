$(function () {

    $(".table-row").click(function() {
        const level = $(this).data("level");
        const percent = $(this).data("percent");
        const oldLevelId = $(this).attr("data-id");
        console.log(oldLevelId);
        $('input[name="level"]').val(level);
        $('input[name="percent"]').val(percent);
        $('input[name="id"]').val(oldLevelId);
        $('.lvl-id').html();
    });

    $('select[name="ref-root"]').val($('#ref-root-info').data('id'));
    
    $('#edit-cmn-ref-root').submit(function (e) {
        e.preventDefault();
        var id = $(this).serializeArray()[0]['value'];
        changeCommonRefRoot(id);
    });

    $('#edit-ref-lvl-form').submit(function (e) {
        e.preventDefault();
        var array = $(this).serializeArray();
        var level = array[0]['value'];
        var oldLevelId = array[1]['value'];
        var percent = array[2]['value'];
        changeRefLevelPercent(level, oldLevelId, percent);
    });

});

function changeRefLevelPercent(refLevel, oldLevelId, percent) {
    var data = "level=" + refLevel + "&oldLevelId=" + oldLevelId + "&percent=" + percent;
    console.log(data);
    $.ajax('/admin/editLevel', {
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        type: 'POST',
        data: data
    }).done(function (e) {
        alert(e['id']);
        $('#_' + refLevel + ' .lvl-percent').html(percent);
        $(".table-row[data-id='" + oldLevelId + "'").attr('data-id', e['id']);
        $('#edit-ref-lvl').modal('hide');
    }).fail(function (error) {
        console.log(JSON.stringify(error));
        alert(error['responseJSON']['error'])
    });
}

function changeCommonRefRoot(id) {
    const data = "id=" + id;
    $.ajax('/admin/editCmnRefRoot', {
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        type: 'POST',
        data: data
    }).done(function (e) {
        const newEmail = $('select option[value="' + id + '"]').html();
        $('#ref-root-info').html(newEmail);
    }).fail(function (e) {
        console.log(e);
        alert("An error occurred!")
    });
}

