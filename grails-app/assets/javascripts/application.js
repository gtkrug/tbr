// This is a manifest file that'll be compiled into application.js.
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better
// to create separate JavaScript files as needed.
//
//= require jquery-2.2.0.min
//= require jquery-ui-1.12.1.min
//= require bootstrap
//= require plupload-2.1.1/js/plupload.full.min.js
//= require plupload-2.1.1/js/moxie.min.js
//= require pagination
//= require tbr
//= require utils
//= require_self

let get = function(url, doSuccess, args)  {
    $.ajax({
        url: url,
        method: 'GET',
        data: args,
        dataType: 'json'
    }).done(function (data){
        doSuccess(data);
    }).fail(function (jqxhr, err){
        console.log('An unexpected error occurred '+err);
    });
}

let list = get;

let add = function(url, doSuccess, args)  {
    $.ajax({
        url: url,
        method: 'PUT',
        data: args,
        dataType: 'json'
    }).done(function (data){
        doSuccess(data);
    }).fail(function (jqxhr, err){
        console.log('An unexpected error occurred '+err);
    });
}

let remove = function(url, doSuccess, args)  {
    $.ajax({
        url: url,
        method: 'DELETE',
        data: args,
        dataType: 'json'
    }).done(function (data){
        doSuccess(data);
    }).fail(function (jqxhr, err){
        console.log('An unexpected error occurred '+err);
    });
}

let update = function(url, doSuccess, args)  {
    $.ajax({
        url: url,
        method: 'POST',
        data: args,
        dataType: 'json'
    }).done(function (data){
        doSuccess(data);
    }).fail(function (jqxhr, err){
        console.log('An unexpected error occurred '+err);
    });
}
