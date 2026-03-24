import React, { useEffect, useState } from 'react'
import { FaChalkboardTeacher, FaSearch, FaUserGraduate } from 'react-icons/fa'
import "./StudentOnlineCourseList.scss"
import { Card, Pagination } from 'antd'
import Meta from 'antd/es/card/Meta'
import { studentCourseList } from '../../../services/studentOnlineCourseService'
import { useNavigate } from 'react-router-dom'
import { appPath } from '../../../config/appPath'
function StudentOnlineCourseList() {
    const tagOption = [
        {
            id: 1,
            name: "ALL"
        }, {
            id: 2,
            name: "ET2021"
        }, {
            id: 3,
            name: "ET5050"
        }, {
            id: 4,
            name: "IT1111"
        }, {
            id: 5,
            name: "ET2555"
        }
    ]
    //list student course
    const [courses, setCourses] = useState([])
    const [totalItems, setTotalItems] = useState(0)
    const [page, setPage] = useState(1);
    const [pageSize] = useState(9);
    const [searchTerm, setSearchTerm] = useState('');

    const fetStudentCourse = (page) => {
        const params = {
            keyword: searchTerm,
            page: page - 1,
            size: pageSize,
        };
        studentCourseList(
            params,
            (data, total) => {
                setCourses(data);
                setTotalItems(total)
                console.log(data)
            },
            () => {
                console.log("error")
            }
        )
    }
    useEffect(() => {
        if (searchTerm) {
            fetStudentCourse(page, searchTerm);
        } else {
            fetStudentCourse(page, '');
        }
    }, [searchTerm, page]);
    const handlePageChange = (page) => {
        setPage(page);
    };
    // search keyword
    const navigate = useNavigate()
    const handleNavigate = (courseId, studentEnrolled) => {
        console.log("courseId ", courseId)
        if (studentEnrolled == true) {
            navigate(`${appPath.studentLearning}/${courseId}`,
                { state: { courseId } }
            )
        } else {
            navigate(`${appPath.CourseWithoutEnroll}/${courseId}/detail`,
                { state: { courseId } }
            )
        }
    }

    return (
        <div className='student-course-containter'>
            <div className='student-course-header'>
                <div className='student-course-search'>
                    <FaSearch className='search-icon'></FaSearch>
                    <input placeholder='Tìm kiếm theo mã học phần hoặc tên khóa học'
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}></input>
                </div>
            </div>
            <div className='student-course-home'>
                <div className='student-course-home-title'>
                    <h1>
                        Danh sách khóa học
                    </h1>
                </div>
                <div className='student-course-tag'>
                    {tagOption.map((item, index) => (
                        <button key={index}>
                            <h3>{item.name}</h3>
                        </button>
                    ))}
                </div>
                <div className='student-course-list'>
                    <div className="row">
                        {courses.map((course, index) => (
                            <div className="col-md-4 mb-4" key={index}>
                                <Card
                                    hoverable
                                    style={{
                                        width: '100%',
                                        height: '400px', // Đảm bảo mỗi Card có cùng chiều cao
                                        display: 'flex',
                                        flexDirection: 'column',
                                        justifyContent: 'space-between',
                                        marginBottom: "15px"
                                    }}
                                    cover={
                                        <img
                                            src={course.courseUrlImg || "default_image_url.jpg"} style={{
                                                width: '100%',
                                                height: '250px', // Cố định chiều cao của ảnh
                                                objectFit: 'cover', // Đảm bảo ảnh không bị méo
                                            }}
                                        />
                                    }
                                    onClick={() => handleNavigate(course.courseId, course.studentEnrolled)}
                                >
                                    <Meta
                                        title={course.courseName}
                                        description={
                                            <div style={{ display: 'flex', flexDirection: 'column', marginTop:'12px'}}>
                                                <span style={{ display: 'flex', alignItems: 'center' , fontSize:'14px'}}>
                                                    <FaChalkboardTeacher style={{ marginRight: '8px' }} />
                                                    {course.teacherName + ', '}
                                                </span>
                                                <span style={{ display: 'flex', alignItems: 'center', marginTop: '6px',fontSize:'14px', color: 'gray' }}>
                                                    <FaUserGraduate style={{ marginRight: '8px' }} />
                                                    Số sinh viên {course.totalStudentEnrolled}
                                                </span>                                          
                                            </div>
                                        }
                                    />
                                </Card>
                            </div>
                        ))}
                    </div>
                    <Pagination
                        current={page}
                        total={totalItems}
                        pageSize={pageSize}
                        onChange={handlePageChange}
                        style={{ marginTop: '20px', textAlign: 'center' }}
                    />
                </div>
            </div>
        </div>
    )
}

export default StudentOnlineCourseList