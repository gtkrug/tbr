let MAX_DISPLAY = 20;
/**
 * wrapper of general purpose paging mechanism
 * @param offset
 * @param max
 * @param total
 * @param callbackFunction
 * @returns {string}
 */
function buildPagination(offset, max, total, callbackFunction){
    return buildPagination(offset, max, total, callbackFunction, true);
}

/**
 * general purpose paging code for paging lists
 * @param numOfColumns
 * @param offset
 * @param max
 * @param total
 * @param callbackFunction
 * @param displayCounts
 * @returns {string}
 */
function buildPagination(offset, max, total, callbackFunction, numOfColumns, displayCounts){
    var html = '';

    // Current design includes pagination inside a table. Need to wrap the pagination div inside a table row and header element
    // to avoid undefined behavior.

    if (numOfColumns) {
        html += '<tr><th colspan="' + numOfColumns + '">';
    } else {
        html += '<tr><th colspan="' + 3 + '">';
    }

    var numOfPages = total / max;
    if (numOfPages > Math.floor(numOfPages)) {
        numOfPages = Math.floor(numOfPages) + 1;
    }
    var pageCount = numOfPages;


    var curPage = getCurrentPage(offset, max);
    console.log("Displaying page "+curPage+" of "+pageCount+" pages.");

    var pagesToDisplay = new Array();
    if( pageCount < 11 ){
        for( var i = 1; i <= pageCount; i++ ){
            pagesToDisplay.push(i);
        }
    }else{
        if( curPage < 6 ){
            for( var i = 1; i < 8; i++ ){
                pagesToDisplay.push(i);
            }
            pagesToDisplay.push(pageCount);
        }else if( curPage > (pageCount-6) ){
            pagesToDisplay.push(1);
            for( var i = (pageCount - 6); i <= pageCount; i++ ){
                pagesToDisplay.push(i);
            }
        }else{
            pagesToDisplay.push(1);
            for( var i = (curPage-2); i < (curPage + 3); i++ ){
                pagesToDisplay.push(i);
            }
            pagesToDisplay.push(pageCount);
        }
    }

    var paginationHtml = '';
    paginationHtml += '<nav aria-label="Page navigation">\n';
     paginationHtml += '<ul class="pagination">';
    if( curPage === 1 ){
        paginationHtml += '<li class="page-item disabled"><a class="page-link" href="#" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>';
    }else{
        var lastOffset = offset - max;
        paginationHtml += '<li class="page-item"><a class="page-link" href="javascript:'+callbackFunction+'('+lastOffset+')" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>';
    }
    for( var i = 0; i < pagesToDisplay.length; i++ ){
        var page = pagesToDisplay[i];
        if( page === curPage ){
            paginationHtml += '<li class="page-item active"><a class="page-link" href="#">'+page+'</a></li>';
        }else{
            var curOffset = (page-1) * max;
            paginationHtml += '<li class="page-item"><a class="page-link" href="javascript:'+callbackFunction+'('+curOffset+')">'+page+'</a></li>';
        }
        if( page === 1 && pagesToDisplay[i+1] !== 2)
            paginationHtml += '<li class="page-item disabled"><a class="page-link" href="#">...</a></li>';
        else if( i < (pagesToDisplay.length - 1) ){
            if( pagesToDisplay[i+1] !== (page + 1) ){
                paginationHtml += '<li class="page-item disabled"><a class="page-link" href="#">...</a></li>';
            }
        }
    }
    if( curPage === pageCount ){
        paginationHtml += '<li class="page-item disabled"><a class="page-link" href="#" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>';
    }else{
        var nextOffset = offset + max;
        paginationHtml += '<li class="page-item"><a class="page-link" href="javascript:'+callbackFunction+'('+nextOffset+')" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>';
    }
    paginationHtml += '</ul>';
    paginationHtml += '</nav>\n';

    if( displayCounts ){
        html += '<div class="row">';
        html += '<div class="col-md-6 d-flex justify-content-end text-muted"><em>\n';
        if( offset + max > total ){
            html += ' Displaying at '+(offset+1)+'-'+total+" of " + total + " items.";
        }else{
            html += ' Displaying at '+(offset+1)+'-'+(offset+max)+" of " + total + " items.";
        }
        html += '</em></div>\n';
        html += '<div class="col-md-6 d-flex justify-content-end">\n';
        html += paginationHtml;
        html += '</div>\n';
        html += '</div>\n';
    }else{
        html += '<div class="row">';
        html += '<div class="col-md-12 d-flex justify-content-end">\n';
        html += paginationHtml;
        html += '</div>\n';
        html += '</div>\n';
    }

    html += '</th></tr>';

    return html;
}

/**
 * get current page based on offset and total items
 * @param offset
 * @param max
 * @returns {number}
 */
function getCurrentPage(offset, max){
    return Math.floor(offset/max) + 1;
}
