import { getRequest, postRequest } from "../api/apiCaller"
import { apiPath } from "../config/apiPath";

export const uploadFileAttachment = async (
    formData, // FormData truyền từ component
    successCallback,
    errorCallback
  ) => {
    console.log(formData); // Kiểm tra dữ liệu trước khi gửi
    await postRequest(
      `${apiPath.uploadFileAttachment}`,
      formData,
      successCallback,
      errorCallback
    );
  };
export const uploadVideo=async(
    formData, // FormData truyền từ component
    successCallback,
    errorCallback
  ) => {
    console.log(formData); // Kiểm tra dữ liệu trước khi gửi
    await postRequest(
      `${apiPath.uploadVideo}`,
      formData,
      successCallback,
      errorCallback
    );
  };
  export const uploadLecture = async (
    params,
    successCallback,
    errorCallback
  ) => {
      await postRequest(
          `${apiPath.uploadLecture}`,
          params,successCallback,errorCallback
      )
  }
  export const getListLecture = async (
    onlineCourseId,
    chapterId,
    params,
    successCallback,
    errorCallback
  ) => {
    const url = `${apiPath.getListLecture}/${onlineCourseId}/chapter/${chapterId}/lecture`;  
    await getRequest(
      url,
      params,
      (response) => {
        if (response?.data) {
          console.log("Lecture data:", response.data); 
          successCallback(response.data); 
        } else {
          console.error("No data in API response");
        }
      },
      errorCallback
    );
  };
  export const getLectureDetail = async (
    lectureId,
    params,
    successCallback,
    errorCallback
  ) => {
    const url = `${apiPath.getLectureDetail}/${lectureId}`;
    await getRequest(
      url,
      params,
      (response) => {
        if (response?.data) {
          console.log("Lecture data:", response.data); 
          successCallback(response.data); 
        } else {
          console.error("No data in API response");
        }
      },
      errorCallback
    );
  };
  
  export const getCourseDetail = async (
    courseId,
    params,
    successCallback,
    errorCallback
  ) => {
    const url = `${apiPath.getDetailCourse}/${courseId}`;
    await getRequest(
      url,
      params,
      (response) => {
        if (response?.data) {
          console.log("detail data:", response.data); 
          successCallback(response.data); 
        } else {
          console.error("No data in API response");
        }
      },
      errorCallback
    );
  };
  
