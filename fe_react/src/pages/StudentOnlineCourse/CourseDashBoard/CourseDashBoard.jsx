import { Card, Progress } from 'antd'
import Meta from 'antd/es/card/Meta'
import React, { useEffect, useState } from 'react'
import "./CourseDashBoard.scss"
import { getMyAllCourses } from '../../../services/studentOnlineCourseService'
import { useNavigate } from 'react-router-dom'
import { appPath } from '../../../config/appPath'

function CourseDashBoard() {
    const [myCourses, setMyCourses] = useState([])
    const fetchMyCourses = () => {
        getMyAllCourses(
            {},
            (data) => {
                setMyCourses(data);
                console.log(data)
            },
            () => {
                console.log("error")
            }
        )
    }
    useEffect(() => {
        fetchMyCourses()
    }, [])
    const navigate = useNavigate()
    const handleNavigate = (courseId) => {
        console.log("courseId ", courseId)
        navigate(`${appPath.studentLearning}/${courseId}`,
            { state: { courseId } }
        )
    }
    const getLastLearningMessage = (lastTimeToLearn) => {
        if (!lastTimeToLearn) {
            return "Bạn chưa học khóa này"
        } else {
            const lastLearnDate = new Date(lastTimeToLearn);
            const currentDate = new Date();
            lastLearnDate.setHours(0, 0, 0, 0);
            currentDate.setHours(0, 0, 0, 0);
            const dayDifference = Math.floor((currentDate - lastLearnDate) / (1000 * 60 * 60 * 24));
            if (dayDifference === 0) {
                return 'Bạn đã học trong ngày hôm nay';
            } else {
                return `Bạn đã học cách đây ${dayDifference} ngày`;
            }
        }
    }
    return (
        <div>
            <div className='my-course-home'>
                <div className='my-course-home-title'>
                    <h1>
                        Khóa học của tôi
                    </h1>
                </div>
                <div className='my-course-list'>
                    <div className="row">
                        {myCourses.map((course, index) => (
                            <div className="col-md-4 mb-4">
                                <Card
                                    hoverable
                                    style={{
                                        width: '100%',
                                        height: '380px',
                                        display: 'flex',
                                        flexDirection: 'column',
                                        justifyContent: 'space-between',
                                        marginBottom: "15px"
                                    }}
                                    cover={
                                        <img src={course.courseInfo.courseUrlImg || "default_image_url.jpg"}
                                            style={{
                                                width: '100%',
                                                height: '250px',
                                                objectFit: 'cover',
                                            }}
                                        />
                                    } onClick={() => handleNavigate(course.courseInfo.courseId)}

                                >
                                    <Meta
                                        title={course.courseInfo.courseName}
                                        description={
                                            <div style={{ display: 'flex', flexDirection: 'column' }}>
                                                <div className='last-time'>
                                                    <p>{getLastLearningMessage(course.courseInfo.lastTimeToLearn)}</p>
                                                </div>                                                
                                                <Progress percent={course.totalCompletionPercent} size="small" />
                                            </div>
                                        }
                                    />
                                </Card>
                            </div>
                        ))}
                    </div>

                </div>
            </div>
        </div>
    )
}

export default CourseDashBoard