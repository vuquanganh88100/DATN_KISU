import React, { useState, useEffect } from 'react';
import { Space, Table } from 'antd';
import { listOnlineCourse } from '../../../services/onlineCourseService';
import ActionButton from '../../../components/ActionButton/ActionButton';
import { useNavigate } from 'react-router-dom';
import { appPath } from '../../../config/appPath';
import "./OnlineCourseList.scss"
import ReactPlayer from 'react-player';
import SearchFilter from '../../../components/SearchFilter/SearchFilter';
export default function OnlineCourseList() {
  const [courses, setCourses] = useState([]);
  const [totalCourses, setTotalCourses] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const navigate = useNavigate()
  const handleEditClick = (courseId) => {
    console.log(" course ID:", courseId);
    navigate(`${appPath.onlineCourseEdit}/${courseId}`)

  };
  const handleLecture = (courseId, publish) => {
    navigate(`/online-course/${courseId}${appPath.lectureAdd}`, {
      state: { courseId, publish },  // Truyền `courseId` và `publish`
    });
  };
  const handleStatistic = (courseId) => {
    navigate(`${appPath.OnlineCourseStatistic}/${courseId}`, { state: { courseId } });
  };
  

  const columns = [
    {
      title: "Đơn vị quản lý",
      dataIndex: "departmentName",
      key: "departmentName",
    },
    {
      title: "Tên môn học",
      dataIndex: "subjectName",
      key: "subjectName",
    },
    {
      title: "Tên khóa học",
      dataIndex: "courseName",
      key: "courseName",
    },
    {
      title: "Trạng thái",
      dataIndex: "publish",
      key: "publish",
      render: (publish) => (
        <span
          style={{
            color: publish ? "green" : "red",
            fontWeight: "bold",
          }}
        >
          {publish ? "Xuất bản" : "Chưa xuất bản"}
        </span>
      ),
    },
    {
      title: "Thao tác",
      render: (_, record) => {
        return (
          <Space size="middle" style={{ cursor: "pointer" }}>
            <ActionButton icon="lecture"
              handleClick={() => handleLecture(record.courseId, record.publish)}
            ></ActionButton>
            <ActionButton
              icon="edit-online-course"
              handleClick={() => handleEditClick(record.courseId)}
            ></ActionButton>
            <ActionButton icon="studentCourse"
            handleClick={()=>handleStatistic(record.courseId)}
            >
            </ActionButton>
          </Space>
        );
      },
    }
  ];

  useEffect(() => {
    fetchCourses();
  }, [page, pageSize]);

  const fetchCourses = () => {
    const params = {
      pageNo: page - 1,
      pageSize: pageSize,
    };

    listOnlineCourse(params, (data, totalElements) => {
      const filteredCourses = data.map(course => ({
        departmentName: course.departmentName,
        subjectName: course.subjectName,
        courseName: course.courseName,
        teacherId: course.teacherId,
        courseId: course.courseId,
        publish: course.publish

      }));
      console.log(data)
      console.log(data.publish)
      setCourses(filteredCourses);
      setTotalCourses(totalElements);
    }, (error) => {
      console.error("Error fetching courses: ", error);
    });
  };

  const handlePageChange = (newPage, newPageSize) => {
    setPage(newPage);
    setPageSize(newPageSize);
  };

  return (
    <div className='online-course-list'>
      <div className='online-course-list-header'>
        <p>Danh sách khóa học online</p>
      </div>
      <div className='search-filter-button'>
        <SearchFilter placeholder="Nhập mã học phần hoặc tên khóa học " />
      </div>
      <div className='online-course-list-wrapper'>
        <Table
          columns={columns}
          dataSource={courses}
          rowKey="courseId"
          className="online-course-list-table"
          pagination={{
            current: page,
            pageSize: pageSize,
            total: totalCourses,
            onChange: handlePageChange,
            showSizeChanger: true,
            pageSizeOptions: ['5', '10', '20'],
          }}
        />
      </div>
    </div>
  );
}
