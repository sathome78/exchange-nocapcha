<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<%----------%>
<script src="<c:url value="/client/js/jquery.form.js"/>"></script>
<%----------%>
<sec:authorize access="<%=AdminController.adminAnyAuthority%>">
<div id="news-list-modal" class="modal fade modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="news.title"/></h4>
            </div>
            <div class="modal-body">
                <div id="all_news_table_wrapper">
                    <table id="all_news_table" class="balance__table">
                        <thead>
                        <tr>
                            <th class="center blue-white"><loc:message code="ordersearch.date"/></th>
                            <th class="center blue-white"><loc:message code="news.newsTitle"/></th>
                            <th class="center blue-white"><loc:message code="news.variants"/></th>
                            <th class="center blue-white"><loc:message code="news.status"/></th>
                        </tr>
                        </thead>

                        <script type="text/template" id="all_news_table_row">
                            <tr>
                                <td><@=date@></td>
                                <td><@=title@></td>
                                <td><@=
                                    (function() {
                                    var output = '';
                                    variants.forEach(function(item) {
                                    var href = '/news/' + resource + id + '/' + item + '/newstopic';
                                    var linkHtml = '<a href='+ href +'>' + item.toUpperCase() + '</a>';
                                    output = output + ' ' + linkHtml;
                                    });
                                    return output

                                    })()
                                    %></td>
                                <td><@=active ? '<loc:message code="news.status.active"/>' : '<loc:message code="news.status.disabled"/>'@></td>
                            </tr>

                        </script>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
</sec:authorize>
