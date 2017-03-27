/**
 * Created by Valk on 04.04.16.
 */

var orderId;
var form;
var operationFunc;
var orderType;

function finPassCheck_old(id, opFunc, oType, event) {
    if (id || (id === 0)) {
        //id:
        // - id order to accept
        // - 0 if new order to be created
        // - id of Modal form to redirect for entering wallet number and confirm output operation
        //store parameters for subsequent call of finPassCheck
        operationFunc = opFunc;
        orderId = id;
        orderType = oType;
        form = $('#submitFinPassForm');
        $('#finPassModal').modal({
            backdrop: 'static'
        }); //modal must call finPassCheck without parameter
    } else {
        //to call finPassCheck for check finpass and subsequent (if check is success) call :
        // - beginAcceptOrder
        // - submitCreateOrder
        // - submitMerchantsOutput
        $.ajax({
            url: '/checkfinpass',
            type: 'POST',
            data: form.serialize(),

            success: function () {
                operationFunc(orderId, orderType);
            },

            error: function (event, jqXHR, options, jsExc) {
                //redirect to settings page on create finpass tab
                var errorType = $.parseJSON(event.responseText).cause;
                var errorMsg = $.parseJSON(event.responseText).detail;
                switch (errorType) {
                    case 'AbsentFinPasswordException':
                    {
                        console.log('AbsentFinPasswordException');
                        window.location.href = '/settings?tabIdx=2&msg=' + errorMsg;
                        break;
                    }
                }
            }
        });
    }
    //it's need on case if button is submit
    if (event) {
        event.preventDefault();
    }
}
/*
 * ORDERS
 *   |_ entering params for create order form: check params and check for enough money
 *          |_ if errors: new order create form: entering params for create order form: check params
 *                              |_ if success: new order submit form
 *          |_ if success: new order submit form
 *                          |_ confirm new order create form: #finPassCheck(${order.id}, #submitCreateOrder, event): enter finpass and ajax to check finpass
 *                                                              |_ if success:  #submitCreateOrder(): ajax to create order
 *                                                                                  |_if success: redirect to success form
 *                                                                                  |_if error: show noty
 *                                                              |_ if error: redirect to create fin password form
 *
 *   |_ accept order button: #beginAcceptOrder(${order.id}): ajax to check for enough money
 *                                  |_ if success: redirect to submit accept form: #finPassCheck(order.id, #submitAcceptOrder): enter finpass and ajax to check finpass
 *                                                                                      |_if success: #submitAcceptOrder(order.id)
 *                                                                                                      |_if success: redirect to success form
 *                                                                                                      |_if error: show noty
 *                                                                                      |_if error:  redirect to create fin password form
 *
 * MERCHANTS
 *    |_entering currency, merchant and summ
 *          |_ #finPassCheck('myModal', #submitMerchantsOutput): enter finpass and ajax to check finpass
 *                    |_if success: #submitMerchantsOutput(modalForm.id): show form to enter wallet number confirm operation
 *                    |_if error:  redirect to create fin password form
 */