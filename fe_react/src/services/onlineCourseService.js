import axios from "axios"
import { getRequest, postRequest, putRequest } from "../api/apiCaller"
import { apiPath } from "../config/apiPath"
export const addOnlineCourse = async (
    params,
    successCallback,
    errorCallback
) => {
    await postRequest(
        `${apiPath.addNewCourse}`,
        params, successCallback, errorCallback
    )
}
export const listOnlineCourse = async (params, successCallback, errorCallback) => {
    await getRequest(
        `${apiPath.listCourse}`,
        params,
        (response) => {
            // Lọc dữ liệu để chỉ lấy các trường cần thiết
            const course = response.data.content;
            console.log(course)
            const totalElements = response.data.totalElements
            successCallback(course, totalElements);
        },
        errorCallback
    );
};
export const getOnlineCourseById = async (
    courseId, params, successCallback, errorCallback) => {
    await getRequest(
        `${apiPath.getCourseById}/${courseId}`,
        params,
        (response) => {
            // Lọc dữ liệu để chỉ lấy các trường cần thiết
            const course = response.data
            console.log(course)
            successCallback(course);
        },
        errorCallback
    )
}
export const editOnlineCourseById = async (
    courseId, params, successCallback, errorCallback) => {
    const url = `${apiPath.getCourseById}/${courseId}`;
    await putRequest(
        url,
        params,
        successCallback, errorCallback

    )
}
export const updatePublish = async (
    courseId, isPublish, successCallback, errorCallback
) => {
    const url = `${apiPath.updatePublish}/${courseId}/publish`;
    const params = { publish: isPublish };

    await putRequest(
        url,
        params,
        successCallback,
        errorCallback
    );
}
export const statisticCourse = async (
    courseId, successCallback, errorCallback) => {
    await getRequest(
        `${apiPath.statisticCourse}/${courseId}`,
        {},
        (response) => {
            const data = response.data
            console.log(data)
            successCallback(data);
        },
        errorCallback
    )
}

export const getLectureProgress=async(
    userId,successCallback,errorCallback
)=>{
    await getRequest(
        `${apiPath.getLectureProgress}/${userId}`,
        {},
        (response) => {
            const data = response.data
            console.log(data)
            successCallback(data);
        },
        errorCallback
    )
}