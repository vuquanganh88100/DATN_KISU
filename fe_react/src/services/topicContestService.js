import { getRequest, postRequest, putRequest, deleteRequest } from '../api/apiCaller';
import { apiPath } from '../config/apiPath';

export const getTopicContests = (params, successCallback, errorCallback) => {
  return getRequest(apiPath.addTopicContest, params, successCallback, errorCallback);
};

export const getTopicContestById = (id, successCallback, errorCallback) => {
  return getRequest(`${apiPath.addTopicContest}/${id}`, {}, successCallback, errorCallback);
};

export const addTopicContest = (params, successCallback, errorCallback) => {
  return postRequest(apiPath.addTopicContest, params, successCallback, errorCallback);
};

export const updateTopicContest = (id, params, successCallback, errorCallback) => {
  return putRequest(`${apiPath.addTopicContest}/${id}`, params, successCallback, errorCallback);
};

export const deleteTopicContest = (id, successCallback, errorCallback) => {
  return deleteRequest(`${apiPath.addTopicContest}/${id}`, {}, successCallback, errorCallback);
};

export const getProblemContestByTopic = (id,params, successCallback, errorCallback) => {
  return getRequest(`${apiPath.getProblemContestByTopic}/${id}`, params, successCallback, errorCallback);
};