import axios from "axios";
import { getRequest, postRequest } from "../api/apiCaller";
import { apiPath } from "../config/apiPath";

export const testCourseAdd = async (
    params,
    successCallback,
    errorCallback
) => {
    await postRequest(
        `${apiPath.uploadTestCourse}`,
        params,
        successCallback,
        errorCallback
    );
};
  
  export const getTestCourseOverview = async (
    testId,
    params,
    successCallback,
    errorCallback
  ) => {
    const url = `${apiPath.getTestCourseOverview}/${testId}`;
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
  export const getTestCourseDetail = async(
    testId,
    params,
    successCallback,
    errorCallback
  ) => {
    const url = `${apiPath.getTestCourseDetail}/${testId}`;
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
  export const submitTestCourse=async(
    params,
    successCallback,
    errorCallback
  )=>{
    const url = `${apiPath.submitTestCourse}`;
    await postRequest(
      url,
      params,
      successCallback,
      errorCallback
    );
  }
  
  export const getTestResult = async(
    testId,
    params,
    successCallback,
    errorCallback
  ) => {
    const url = `${apiPath.getTestCourseResult}/${testId}`;
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
  export const exportPdfTestCourse = async (testCourseId, params, successCallback, errorCallback) => {
    const url = `${apiPath.downloadTestCourse}/${testCourseId}`;

    try {
        const response = await axios.get(url, {
            params: params,
            responseType: 'blob', 
        });

        const blob = new Blob([response.data], { type: 'application/pdf' });
        const link = document.createElement('a');
        const objectUrl = window.URL.createObjectURL(blob);
        link.href = objectUrl;
        link.download = 'questions.pdf';
        document.body.appendChild(link);
        link.click();
        link.remove();

        window.URL.revokeObjectURL(objectUrl);

        if (successCallback) successCallback(response);
    } catch (error) {
        console.error('Error exporting PDF:', error);
        if (errorCallback) errorCallback(error);
    }
};
