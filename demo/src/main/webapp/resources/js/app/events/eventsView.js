'use strict';
define([
    'angular',
    'underscore',
    'configuration',
    'bootstrap',
    'angularRoute',
    'angularResource',
    'app/api/services'
], function(angular, _, config) {
    angular.module('ticketMonster.eventsView', ['ngRoute', 'ngResource', 'ticketMonster.api'])
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
                	if(!Modernizr.touch) {
	                    $(el).popover({
	                        trigger: 'hover',
	                        container: '#content',
	                        content: attrs.content,
	                        title: attrs.originalTitle
                        }).data('bs.popover')
                            .tip()
                            .addClass('visible-lg')
                            .addClass('visible-md');
                	}
                }
            };
        })
        .controller('EventsController', ['$scope','EventResource', function($scope, EventResource) {
        	$scope.config = config;
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