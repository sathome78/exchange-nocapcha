/**
 * Created by OLEG on 17.10.2016.
 */


$(function () {
   var walletId = $('#walletId').text();
   var requestUrl = '/admin/getStatements?walletId=' + walletId;
   var statementsDataTable = $('#user-statements-table').DataTable({
      "serverSide": true,
      "ajax": {
         "url": requestUrl,
         "type": "GET",
         "dataSrc": "data"
      },
      "paging": true,
      "info": true,
      "bFilter": false,
      "bSort" : false,
      "columns": [
         {
            "data": "datetime"
         },
         {
            "data": "activeBalanceBefore"
         },
         {
            "data": "reservedBalanceBefore"
         },
         {
            "data": "operationType"
         },
         {
            "data": "amount"
         },
         {
            "data": "commissionAmount"
         },
         {
            "data": "sourceType"
         },
         {
            "data": "activeBalanceAfter"
         },
         {
            "data": "reservedBalanceAfter"
         }


      ],
      "order": [
          0, "asc"
      ]
   });

});