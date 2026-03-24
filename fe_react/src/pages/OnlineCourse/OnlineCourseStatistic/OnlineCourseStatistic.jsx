import { Table, Modal } from 'antd';
import React, { useEffect, useState } from 'react';
import SearchFilter from '../../../components/SearchFilter/SearchFilter';
import { useLocation } from 'react-router-dom';
import { statisticCourse, getLectureProgress } from '../../../services/onlineCourseService';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import "./OnlineCourseStatistic.scss";

function OnlineCourseStatistic() {
  const location = useLocation();
  const courseId = location.state?.courseId;
  const [studentCourseData, setStudentCourseData] = useState([]);
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [lectureProgress, setLectureProgress] = useState(null);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [selectedStudent, setSelectedStudent] = useState(null);

  useEffect(() => {
    if (courseId) {
      statisticCourse(
        courseId,
        (data) => {
          const transformedData = data.map((item, index) => ({
            key: index,
            studentCode: item.studentCode,
            studentName: `${item.firstName} ${item.lastName}`,
            courseName: item.onlineCourseDto?.courseName || "N/A",
            studentProgress: `${item.completedPercent}%`,
            lastAccess: item.onlineCourseDto.lastTimeToLearn 
              ? new Date(item.onlineCourseDto.lastTimeToLearn).toLocaleDateString("vi-VN")
              : "Chưa truy cập",
            userId: item.userId 
          }));
          setStudentCourseData(transformedData);
        },
        (error) => {
          console.log(error);
        }
      );
    }
  }, [courseId]);

  const handleStudentClick = (userId, studentData) => {
    setSelectedUserId(userId);
    setSelectedStudent(studentData);
    setIsModalVisible(true);
    
    getLectureProgress(
      userId,
      (data) => {
        const chartData = Object.entries(data).map(([sequence, info]) => ({
          sequence: `Bài ${sequence}`,
          percentWatchedTime: info.percentWatchedTime !== null ? Number(info.percentWatchedTime).toFixed(2) : null,
          percentCorrectAns: info.percentCorrectAns !== null ? Number(info.percentCorrectAns).toFixed(2) : null,
          lectureName: info.lectureName || `Bài ${sequence}`
        }));
        console.log('Chart data:', chartData);
        setLectureProgress(chartData);
      },
      (error) => {
        console.error("Error fetching lecture progress:", error);
      }
    );
  };

  const columns = [
    {
      title: "Mã sinh viên",
      dataIndex: "studentCode",
      key: "studentCode",
      render: (text, record) => (
        <span
          className="student-code-link"
          onClick={() => handleStudentClick(record.userId, record)}
          style={{ cursor: 'pointer', color: '#1890ff' }}
        >
          {text}
        </span>
      )
    },
    {
      title: "Họ và tên",
      dataIndex: "studentName",
      key: "studentName"
    },
    {
      title: "Tên khóa học",
      dataIndex: "courseName",
      key: "courseName"
    },
    {
      title: "Tiến độ hoàn thành",
      dataIndex: "studentProgress",
      key: "studentProgress"
    },
    {
      title: "Lần cuối truy cập",
      dataIndex: "lastAccess",
      key: "lastAccess"
    }
  ];

  const handleModalClose = () => {
    setIsModalVisible(false);
  };

  return (
    <div className='student-online-course-list'>
      <div className='student-online-course-list-header'>
        <p>Danh sách sinh viên đăng ký khóa học</p>
      </div>
      <div className='search-filter-button'>
        <SearchFilter placeholder="Nhập mã sinh viên hoặc tên sinh viên" />
      </div>
      <div className='student-online-course-list-wrapper'>
        <Table
          className="student-online-course-list-table"
          columns={columns}
          dataSource={studentCourseData}
          pagination={{ pageSize: 10 }}
          rowKey="studentCode"
        />
      </div>

      <Modal
        title={`Tiến độ học tập - ${selectedStudent?.studentName || ''}`}
        open={isModalVisible}
        onCancel={handleModalClose}
        width={800}
        footer={null}
      >
        {lectureProgress ? (
          <div style={{ width: '100%', height: 400 }}>
            <ResponsiveContainer>
              <LineChart
                data={lectureProgress}
                margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                connectNulls={false}
              >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis 
                  dataKey="sequence"
                  tick={{ fontSize: 12 }}
                />
                <YAxis
                  tick={{ fontSize: 12 }}
                  label={{ 
                    value: 'Phần trăm (%)', 
                    angle: -90, 
                    position: 'insideLeft',
                    style: { fontSize: 12 }
                  }}
                />
                <Tooltip
                  content={({ active, payload, label }) => {
                    if (active && payload && payload.length) {
                      return (
                        <div className="custom-tooltip" style={{ 
                          backgroundColor: 'white', 
                          padding: '10px', 
                          border: '1px solid #ccc' 
                        }}>
                          <p><b>{label}</b></p>
                          <p>{payload[0]?.payload.lectureName}</p>
                          {payload[0]?.value && (
                            <p style={{ color: '#8884d8' }}>
                              Thời gian xem: {payload[0].value}%
                            </p>
                          )}
                          {payload[1]?.value && (
                            <p style={{ color: '#82ca9d' }}>
                              Trả lời đúng: {payload[1].value}%
                            </p>
                          )}
                        </div>
                      );
                    }
                    return null;
                  }}
                />
                <Legend />
                <Line
                  type="monotone"
                  dataKey="percentWatchedTime"
                  name="Thời gian xem"
                  stroke="#8884d8"
                  strokeWidth={2}
                  connectNulls={false}
                />
                <Line
                  type="monotone"
                  dataKey="percentCorrectAns"
                  name="Trả lời đúng"
                  stroke="#82ca9d"
                  strokeWidth={2}
                  connectNulls={false}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        ) : (
          <div>Đang tải dữ liệu...</div>
        )}
      </Modal>
    </div>
  );
}

export default OnlineCourseStatistic;