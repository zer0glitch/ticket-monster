'use strict';
define([
    'angular',
    'underscore',
    'bootstrap',
    'angularRoute',
    'angularResource'
], function(angular, _) {
    angular.module('ticketMonster.eventDetailView', ['ngRoute', 'ngResource'])
        .config(['$routeProvider', function($routeProvider) {
            $routeProvider.when('/events/:eventId', {
                templateUrl: 'resources/js/app/eventDetail/eventDetail.html',
                controller: 'EventDetailController'
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
        .factory('ShowResource', function($resource){
            var resource = $resource('rest/shows/:showId',{showId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
            return resource;
        })
        .factory('ShowResource', function($resource){
            var resource = $resource('rest/shows/:showId',{showId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
            return resource;
        })
        .controller('EventDetailController', ['$scope', '$routeParams', '$location', 'EventResource', 'ShowResource', function($scope, $routeParams, $location, EventResource, ShowResource) {
            EventResource.get({eventId:$routeParams.eventId}, function(data) {
                $scope.event = data;
                ShowResource.queryAll({event:$scope.event.id}, function(data) {
                    console.log("Fetched Shows");
                    $scope.shows = data;
                }, function() {
                    console.log("Failed to fetch shows");
                });
            }, function() {
                console.log("failure");
            });

            $scope.$watch('selectedShow', function(newValue, oldValue) {
                if(newValue == null) {
                    $scope.selectedPerformanceDays = [];
                } else {
                    $scope.selectedPerformanceDays = newValue.performances;
                    $scope.selectedPerformanceDay = $scope.selectedPerformanceDays[0];
                }
                $scope.selectedPerformances = [];
                $scope.selectedPerformance = {};
            });

            $scope.$watch('selectedPerformanceDay', function(newValue, oldValue) {
                if(newValue != null) {
                    $scope.selectedPerformances = _.filter($scope.selectedShow.performances, function(performance) {
                        var performanceDay = new Date(performance.date).setHours(0, 0, 0, 0);
                        var chosenDay = new Date(newValue.date).setHours(0, 0, 0, 0);
                        return chosenDay.valueOf() === performanceDay.valueOf();
                    });
                    $scope.selectedPerformance = $scope.selectedPerformances[0];
                }
            });

            $scope.beginBooking = function() {
                $location.path('/book/' + $scope.selectedShow.venue.id + '/' + $scope.selectedPerformance.id);
            }
        }]);
});