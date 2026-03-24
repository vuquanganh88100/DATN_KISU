import "./ExamClassEdit.scss";
import React, { useEffect, useState } from "react";
import useNotify from "../../../hooks/useNotify";
import dayjs from "dayjs";
import { updateExamClassService } from "../../../services/examClassServices";
import { useLocation, useNavigate } from "react-router-dom";
import useExamClasses from "../../../hooks/useExamClass";
import { Skeleton } from "antd";
import UpdateExamClassInfoForm from "../components/UpdateExamClassInfoForm/UpdateExamClassInfoForm";
const ExamClassEdit = () => {
  const navigate = useNavigate();
  const { getExamClassDetail, examClassInfo, infoLoading } = useExamClasses();
  const [selectedTestId, setSelectedTestId] = useState(null);
  const [lstStudentId, setLstStudentId] = useState(null);
  const [lstSupervisorId, setLstSupervisorId] = useState([]);
  const [loading, setLoading] = useState(false);
  const notify = useNotify();
  const location = useLocation();
  const id = location.pathname.split("/")[2];
  useEffect(() => {
    getExamClassDetail(id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
  const onFinish = (value) => {
    setLoading(true);
    updateExamClassService(
        id,
        {
            ...value,
            testId: selectedTestId ?? examClassInfo.testId,
            examineTime: dayjs(value.examineTime).format(
                "HH:mm DD/MM/YYYY"
            ),
            lstStudentId: lstStudentId ? lstStudentId :
                (examClassInfo.lstStudentId ? examClassInfo.lstStudentId.split(",").map(Number) : []),
            lstSupervisorId: lstSupervisorId && lstSupervisorId.length > 0 ? lstSupervisorId
                : (examClassInfo.lstSupervisorId ? examClassInfo.lstSupervisorId.split(",").map(Number) : [])
        },
        () => {
            setLoading(false);
            notify.success("Cập nhật lớp thi thành công!");
            navigate("/exam-class-list")
        },
        () => {
            setLoading(false);
             notify.error("Lỗi cập nhật lớp thi!");
        }
    ).then(() => {});
  };
  return (
    <div className="exam-class-edit">
      <Skeleton active loading={infoLoading}>
        <UpdateExamClassInfoForm
          infoHeader="Cập nhật lớp thi"
          semesterDisabled={true}
          onFinish={onFinish}
          btnText="Cập nhật"
          initialValues={{
              remember: false,
              subjectId: examClassInfo ? examClassInfo.subjectId : null,
              semesterId: examClassInfo ? examClassInfo.semesterId : null,
              roomName: examClassInfo ? examClassInfo.roomName : null,
              examineTime: examClassInfo.examineTime ? dayjs(`${examClassInfo.examineTime} ${examClassInfo.examineDate}`, "HH:mm DD-MM-YYYY") : "",
              code: examClassInfo ? examClassInfo.code : null,
              testId: examClassInfo ? examClassInfo.testId : null,
              testType: (examClassInfo?.testType ?? "").toUpperCase(),
              existedResult: examClassInfo?.existedResult
          }}
          testDisplay={`${examClassInfo.testName} - ${examClassInfo.duration ?? 0} phút - ${examClassInfo.lstTestSetCode ? examClassInfo.lstTestSetCode.split(",").length : 0} mã đề`}
          lstStudentId={
            examClassInfo && examClassInfo.lstStudentId
              ? examClassInfo.lstStudentId.split(",").map(Number)
              : []
          }
          lstSupervisorId={
            examClassInfo && examClassInfo.lstSupervisorId
              ? examClassInfo.lstSupervisorId
                .split(",")
                .map(Number)
              : []
          }
          loading={loading}
          testId={examClassInfo.testId}
          onSelectTestId={(id) => setSelectedTestId(id)}
          onSelectStudents={(ids) => setLstStudentId(ids)}
          onSelectTeachers={(ids) => setLstSupervisorId(ids)}
          testType = {(examClassInfo?.testType ?? "").toUpperCase()}
          action="EDIT"
        />
      </Skeleton>
    </div>
  );
};
export default ExamClassEdit;
