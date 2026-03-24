import React, { useState, useEffect } from 'react';
import { Table, Space, Button, Input, message, Card, Tag, Row, Col, Typography, Progress } from 'antd';
import { useNavigate } from 'react-router-dom';
import { Search, Filter, BookOpen, CheckCircle, Clock, Trophy, XCircle } from 'lucide-react';
import './StudentContestList.scss';
import { getStudentProblemContests } from '../../../services/problemContestService';

const { Title, Text } = Typography;

export default function ProgrammingProblemList() {
    const [allProblems, setAllProblems] = useState([]);
    const [displayedProblems, setDisplayedProblems] = useState([]);
    const [page, setPage] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [totalProblems, setTotalProblems] = useState(0);
    const [loading, setLoading] = useState(false);
    const [searchText, setSearchText] = useState('');
    const [selectedTopic, setSelectedTopic] = useState('All');
    const navigate = useNavigate();

    const normalizeStatus = (rawStatus) => {
        if (!rawStatus) return 'NONE';
        const s = String(rawStatus).trim().toUpperCase();
        if (['AC', 'ACCEPTED', 'DONE', 'COMPLETED', 'SUCCESS', 'SOLVED'].includes(s)) return 'AC';
        if (['WA', 'WRONG', 'WRONG_ANSWER', 'FAILED', 'INCORRECT'].includes(s)) return 'WA';
        if (['NONE', 'NOT_SOLVED', 'UNSOLVED', 'PENDING', 'NO_SUBMISSION', 'UNATTEMPTED'].includes(s)) return 'NONE';
        return s;
    };

    const mapApiData = (apiData) => {
    const LEVEL_MAP = {
        easy: 'Dễ',
        medium: 'Trung bình',
        hard: 'Khó'
    };

    return apiData.map(item => ({
        key: item.id,
        id: item.id,
        title: item.title,
        category: item.topicContestName || 'General',
        level: LEVEL_MAP[item.level] || item.level,
        percentAC: item.acRate ,
        numsSubmit: item.totalSubmissions ,
        status: normalizeStatus(item.status)
    }));
};


    const topics = ['All', ...new Set(allProblems.map(p => p.category))];

    const fetchProblems = () => {
        setLoading(true);
        getStudentProblemContests(
            null,
            (response) => {
                const apiData = Array.isArray(response?.data?.content) ? response.data.content : [];
                const mapped = mapApiData(apiData);
                console.log(mapped)
                setAllProblems(mapped);
                setTotalProblems(mapped.length);
                setLoading(false);
            },
            (error) => {
                console.error("Error fetching problems:", error);
                message.error("Không thể tải danh sách bài thi");
                setLoading(false);
            }
        );
    };

    useEffect(() => {
        fetchProblems();
    }, []);

    useEffect(() => {
        const filtered = allProblems.filter(prob => {
            const matchesSearch = prob.title.toLowerCase().includes(searchText.toLowerCase()) ||
                                prob.id.toString().includes(searchText);
            const matchesTopic = selectedTopic === 'All' || prob.category === selectedTopic;
            return matchesSearch && matchesTopic;
        });
        const start = (page - 1) * pageSize;
        const end = start + pageSize;
        setDisplayedProblems(filtered.slice(start, end));
        setTotalProblems(filtered.length);
    }, [allProblems, page, pageSize, searchText, selectedTopic]);

    const handleSearch = (e) => {
        setSearchText(e.target.value);
        setPage(1);
    };

    const handlePageChange = (newPage, newPageSize) => {
        setPage(newPage);
        setPageSize(newPageSize);
    };

    const navigateToDetail = (problem) => {
        navigate(`/student-problem-contest/detail/${problem.id}`, { state: { problem } });
    };

    const getLevelColor = (level) => {
        switch (level) {
            case 'Dễ': return '#22c55e';
            case 'Trung bình': return '#eab308';
            case 'Khó': return 'var(--hust-color)';
            default: return '#64748b';
        }
    };

    const columns = [
        {
            title: 'Trạng thái',
            dataIndex: 'status',
            key: 'status',
            width: '120px',
            render: (status) => (
                <Space>
                    {status === 'AC' && (
                        <>
                            <CheckCircle size={18} color="#22c55e" />
                            <Text type="success" style={{ fontSize: '12px' }}>Hoàn thành</Text>
                        </>
                    )}
                    {status === 'WA' && (
                        <>
                            <XCircle size={18} color="#ef4444" />
                            <Text type="danger" style={{ fontSize: '12px' }}>Giải sai</Text>
                        </>
                    )}
                    {status !== 'AC' && status !== 'WA' && (
                        <>
                            <Clock size={18} color="#64748b" />
                            <Text type="secondary" style={{ fontSize: '12px' }}>Chưa giải</Text>
                        </>
                    )}
                </Space>
            )
        },
        {
            title: 'Tiêu đề',
            dataIndex: 'title',
            key: 'title',
            render: (text, record) => (
                <div className="problem-title-cell">
                    <a onClick={() => navigateToDetail(record)} className="problem-link">
                        {text}
                    </a>
                </div>
            )
        },
        {
            title: 'Chủ đề',
            dataIndex: 'category',
            key: 'category',
            width: '160px',
        },
        {
            title: 'Mức độ',
            dataIndex: 'level',
            key: 'level',
            width: '120px',
            render: (level) => (
                <span style={{ color: getLevelColor(level), fontWeight: 600 }}>{level}</span>
            )
        },
        {
            title: 'Tỉ lệ AC',
            dataIndex: 'percentAC',
            key: 'percentAC',
            width: '120px',
            align: 'center',
        },
        {
            title: 'Số bài nộp',
            dataIndex: 'numsSubmit',
            key: 'numsSubmit',
            width: '120px',
            align: 'center',
        }
    ];

    return (
        <div className="programming-problem-list-container">
            <Row gutter={[24, 24]}>
                <Col xs={24} lg={18}>
                    {/* Header Cards */}
                    <div className="contest-header-banner">
                        <Card className="welcome-card" bordered={false}>
                            <Row align="middle">
                                <Col flex="auto">
                                    <Title level={2}>Danh sách bài thi lập trình</Title>
                                    <Text type="secondary">Thử thách bản thân với các bài toán lập trình đa dạng và nâng cao kỹ năng của bạn.</Text>
                                </Col>
                                <Col>
                                    <Trophy size={48} className="header-icon" />
                                </Col>
                            </Row>
                        </Card>
                    </div>

                    {/* Topics Row */}
                    <div className="topics-row-container">
                        <div className="topics-scroll">
                            {topics.map(topic => (
                                <Tag.CheckableTag
                                    key={topic}
                                    checked={selectedTopic === topic}
                                    onChange={() => {
                                        setSelectedTopic(topic);
                                        setPage(1);
                                    }}
                                    className={`topic-tag ${selectedTopic === topic ? 'active' : ''}`}
                                >
                                    {topic}
                                </Tag.CheckableTag>
                            ))}
                        </div>
                    </div>

                    {/* Filter & Search */}
                    <div className="list-controls">
                        <Input
                            placeholder="Tìm kiếm theo ID hoặc tên bài thi..."
                            prefix={<Search size={18} className="search-icon" />}
                            onChange={handleSearch}
                            className="modern-search-input"
                            allowClear
                        />
                    </div>

                    {/* Table */}
                    <Card className="table-card" bordered={false}>
                        <Table
                            columns={columns}
                            dataSource={displayedProblems}
                            loading={loading}
                            pagination={{
                                current: page,
                                pageSize: pageSize,
                                total: totalProblems,
                                onChange: handlePageChange,
                                showSizeChanger: true,
                                pageSizeOptions: ['10', '20', '50'],
                                showTotal: (total) => `Tổng cộng ${total} bài thi`,
                            }}
                            rowKey="id"
                            className="modern-table"
                        />
                    </Card>
                </Col>

                <Col xs={24} lg={6}>
                    <div className="side-panel">
                        <Card title={<Space><BookOpen size={18} /><span>Tiến độ của tôi</span></Space>} bordered={false} className="stats-card">
                            <div className="stat-item">
                                <div className="stat-label">Đã hoàn thành</div>
                                <div className="stat-value">12/50</div>
                                <Progress percent={24} strokeColor="#22c55e" showInfo={false} />
                            </div>
                            <div className="stat-item">
                                <div className="stat-label">Dễ</div>
                                <div className="stat-value">8/20</div>
                                <Progress percent={40} strokeColor="#22c55e" size="small" />
                            </div>
                            <div className="stat-item">
                                <div className="stat-label">Trung bình</div>
                                <div className="stat-value">4/20</div>
                                <Progress percent={20} strokeColor="#eab308" size="small" />
                            </div>
                            <div className="stat-item">
                                <div className="stat-label">Khó</div>
                                <div className="stat-value">0/10</div>
                                <Progress percent={0} strokeColor="var(--hust-color)" size="small" />
                            </div>
                        </Card>

                        <Card title={<Space><Clock size={18} /><span>Bài thi gần đây</span></Space>} bordered={false} className="recent-card">
                            <ul className="recent-list">
                                <li>
                                    <div className="recent-item">
                                        <Text ellipsis className="recent-title">Two Sum</Text>
                                        <Text type="secondary" className="recent-time">2 giờ trước</Text>
                                    </div>
                                </li>
                                <li>
                                    <div className="recent-item">
                                        <Text ellipsis className="recent-title">Add Two Numbers</Text>
                                        <Text type="secondary" className="recent-time">Hôm qua</Text>
                                    </div>
                                </li>
                            </ul>
                        </Card>
                    </div>
                </Col>
            </Row>
        </div>
    );
}


