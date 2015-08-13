'use strict';
define([
    'angular',
    'underscore',
    'bootstrap',
    'angularRoute',
    'angularResource'
], function(angular, _) {
    angular.module('ticketMonster.eventsView', ['ngRoute', 'ngResource'])
        .config(['$routeProvider', function($routeProvider) {
            $routeProvider.when('/events', {
                templateUrl: 'resources/js/app/events/events.html',
                controller: 'EventsController'
            });
        }])
        .directive('menuPopover', function () {
            return {
                restrict: 'A',
                template: '',
                link: function(scope, el, attrs) {
                    $(el).popover({
                        trigger: 'hover',
                        container: '#content',
                        content: attrs.content,
                        title: attrs.originalTitle
                    });
                }
            };
        })
        .factory('EventResource', function($resource){
            var resource = $resource('rest/events/:eventId',{eventId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
            return resource;
        })
        .controller('EventsController', ['$scope','EventResource', function($scope, EventResource) {
            $scope.events = EventResource.queryAll(function(data) {
                $scope.events = data;
                $scope.categories = _.uniq(
                    _.map($scope.events, function(event){
                        return event.category;
                    }), false, function(item){
                        return item.id;
                    });
                $('.carousel-inner').find('.item:first').addClass('active');
                $(".carousel").carousel();
            });
        }]);
});