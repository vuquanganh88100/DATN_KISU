import dayjs from "dayjs";
import React, { useState } from "react";
import useNotify from "../../../hooks/useNotify";
import { addExamClassService } from "../../../services/examClassServices";
import UpdateExamClassInfoForm from "../components/UpdateExamClassInfoForm/UpdateExamClassInfoForm"
import "./ExamClassCreate.scss";
import { useNavigate } from "react-router-dom";
const ExamClassAdd = () => {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false);
  const [selectedTestId, setSelectedTestId] = useState(null);
  const [lstStudentId, setLstStudentId] = useState([]);
  const [lstSupervisorId, setLstSupervisorId] = useState([]);
  const notify = useNotify();
  const onFinish = (value) => {
    setLoading(true);
    addExamClassService(
        {
            ...value,
            examineTime: dayjs(value.examineTime).format(
                "HH:mm DD/MM/YYYY"
            ),
            testId: selectedTestId,
            lstStudentId: lstStudentId,
            lstSupervisorId: lstSupervisorId,
            fromExamClassId: value?.fromExamClassId ? value?.fromExamClassId : null
        },
        (res) => {
            setLoading(false);
            notify.success("Thêm mới lớp thi thành công!");
            navigate("/exam-class-list")
        },
        (error) => {
            setLoading(false);
            notify.error("Lỗi thêm mới lớp thi!");
        }
    ).then(() => {});
  };
  return (
    <div className="exam-class-add">
      <UpdateExamClassInfoForm
        infoHeader="Thêm lớp thi"
        onFinish={onFinish}
        btnText="Thêm"
        initialValues={{ remember: false }}
        loading={loading}
        onSelectTestId={(id) => setSelectedTestId(id)}
        onSelectStudents={(ids) => setLstStudentId(ids)}
        onSelectTeachers={(ids) => setLstSupervisorId(ids)}
        action="CREATE"
      />
    </div>
  );
};
export default ExamClassAdd;
