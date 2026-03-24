import React, { useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom';
import { enrollCourse, getCourseDetail } from '../../../../services/studentOnlineCourseService';
import { Button, Col, Collapse, Row } from 'antd';
import "./StudentOnlineCourseDetail.scss"
import useNotify from '../../../../hooks/useNotify';

const { Panel } = Collapse;

function StudentOnlineCourseDetail() {
  const notify = useNotify()
  const location = useLocation();
  const { courseId } = location.state || {}
  const [courseDetail, setCourseDetail] = useState(null)
  const [lectureContent, setLectureContent] = useState([])
  const [testCourseContent, setTestCourseContent] = useState([])
  const [totalTime, setTotalTime] = useState()
  useEffect(() => {
    const fetchCourseDetail = () => {
      getCourseDetail(
        courseId,
        {},
        (data) => {
          console.log(data)
          setCourseDetail(data);
          setLectureContent(data.chapterLectureMap)
          setTestCourseContent(data.testCourseLectureMap)
          setTotalTime(data.totalVideoDuration)
        },
        () => {
          console.log("error");
        }
      );
    };

    if (courseId) {
      fetchCourseDetail();
    }
    console.log(courseId)
  }, [courseId]);
  // console.log(lectureContent)
  const videoDuration = () => {
    const hours = Math.floor(totalTime / 3600)
    const minitues = Math.floor((totalTime % 3600) / 60)
    return `${hours} giờ ${minitues} phút`
  }
  const enrollNewCourse = () => {
    enrollCourse(
      { courseId },
      () => {
        notify.success("Đăng ký khóa học thành công")
      },
      (error) => {
        console.log('error : ', error)
        notify.error("Lỗi đăng ký khóa học")
      }
    )
  }
  return (
    <Row>
      <Col span={15}>
        <div className='course-detail-container'>
          <div className='course-name'>
            <h1>
              {courseDetail?.courseInfo?.courseName}
            </h1>
          </div>
          <div className='course-description'>
            <div
              dangerouslySetInnerHTML={{ __html: courseDetail?.courseInfo?.courseDescription }}
            />
          </div>

          <div className='course-content'>
            <div className='course-content-header'>
              <div className='course-content-header-sticky'>
                <h3>
                  Nội  dung khóa học
                </h3>
              </div>
              <div className='course-content-sub-header'>
                <ul>
                  <li>{courseDetail?.totalChapter} Chương</li>
                  <li>{courseDetail?.totalLecture} Bài giảng</li>
                  <li>{courseDetail?.totalQuestion} Câu hỏi</li>
                  <li>Thời lượng {videoDuration()}</li>
                </ul>
              </div>
            </div>
            <div className='course-conten-lecture'>
              <Collapse accordion={false} expandIconPosition="start">
                {Object.entries(lectureContent).map(([chapterName, lectures]) => {
                  // Lấy danh sách test tương ứng từ testCourseContent
                  const tests = testCourseContent[chapterName] || [];

                  return (
                    <Panel header={chapterName} key={chapterName}>
                      <div className="panel-body">
                        <ul style={{ listStyleType: "none", padding: 0 }}>
                          {lectures.map((lecture, index) => (
                            <li key={index} style={{ color: "blue" }}>
                              📘 Bài {index + 1}: {lecture.lectureName}
                            </li>
                          ))}
                        </ul>
                        <ul style={{ listStyleType: "none", padding: 0 }}>
                          {tests.map((test, index) => (
                            <li key={index} style={{ color: "orange" }}>
                              📝 {test.name}
                            </li>
                          ))}
                        </ul>

                      </div>
                    </Panel>
                  );
                })}
              </Collapse>

            </div>
          </div>
        </div>
      </Col>
      <Col span={9}>
  <div className='enroll-course-container'>
    <div className='image-preview'>
      <img src={courseDetail?.courseInfo?.courseUrlImg} alt="" />
      
      <div className="course-info">
        <div className="course-stats">
          <div className="stat-item">
            <div className="stat-value">{courseDetail?.courseInfo.totalStudentEnrolled}</div>
            <div className="stat-label">Học viên</div>
          </div>
          <div className="stat-item">
            <div className="stat-value">5</div>
            <div className="stat-label">Bài kiểm tra</div>
          </div>
          <div className="stat-item">
            <div className="stat-value">{courseDetail?.totalLecture}</div>
            <div className="stat-label">Bài học</div>
          </div>
        </div>

        <div className="course-features">
          <div className="feature-item">
            <span className="feature-icon">✓</span>
            <span>Học trực tuyến không giới hạn</span>
          </div>
          <div className="feature-item">
            <span className="feature-icon">✓</span>
            <span>Giáo trình chi tiết</span>
          </div>
          <div className="feature-item">
            <span className="feature-icon">✓</span>
            <span>Hỗ trợ 24/7</span>
          </div>
        </div>
      </div>

      <Button onClick={enrollNewCourse}>
        Đăng ký khóa học
      </Button>
    </div>
  </div>
</Col>
    </Row>
  )
}

export default StudentOnlineCourseDetail