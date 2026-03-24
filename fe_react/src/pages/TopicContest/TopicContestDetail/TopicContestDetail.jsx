import React, { useState, useEffect } from 'react';
import { Table, Button, Card, message, Spin, Empty, Breadcrumb } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { useNavigate, useParams } from 'react-router-dom';
import { appPath } from '../../../config/appPath';
import { getTopicContestById, getProblemContestByTopic } from '../../../services/topicContestService';
import './TopicContestDetail.scss';

export default function TopicContestDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [topicContest, setTopicContest] = useState(null);
  const [problems, setProblems] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchTopicContestDetail();
  }, [id]);

  const fetchTopicContestDetail = () => {
    setLoading(true);

    // Fetch topic contest info
    getTopicContestById(
      id,
      (response) => {
        const topicData = Array.isArray(response) ? response[0] : response?.data || response;
        setTopicContest(topicData);
      },
      (error) => {
        console.error("Error fetching topic contest:", error);
        message.error("Lỗi khi tải thông tin chủ đề");
      }
    );

    // Fetch problems by topic
    getProblemContestByTopic(
      id,
      {},
      (response) => {
        try {
          const apiData = Array.isArray(response?.data) ? response.data : (Array.isArray(response) ? response : []);

          const mappedProblems = apiData.map((item, index) => ({
            key: item.id?.toString() || index.toString(),
            problemId: item.id,
            ordinal: index + 1,
            name: item.title || item.name || 'N/A',
            difficulty: item.level || item.difficulty || 'N/A',
            totalSubmissions: item.totalSubmissions || 0,
            acceptedCount: item.totalAC || 0,
            acRate: item.acRate || 0,
          }));

          setProblems(mappedProblems);
          setLoading(false);
        } catch (err) {
          console.error("Error mapping problems data:", err);
          message.error("Lỗi khi xử lý dữ liệu");
          setProblems([]);
          setLoading(false);
        }
      },
      (error) => {
        console.error("Error fetching problems:", error);
        message.error("Lỗi khi tải danh sách đề thi");
        setProblems([]);
        setLoading(false);
      }
    );
  };

  const handleProblemStatisticClick = (problemId, problemName) => {
    navigate(`${appPath.topicContestProblemStatistic}/${problemId}`, {
      state: { problemName },
    });
  };

  const columns = [
    {
      title: "Số thứ tự",
      dataIndex: "ordinal",
      key: "ordinal",
      width: "8%",
      align: "center",
    },
    {
      title: "Tên đề thi",
      dataIndex: "name",
      key: "name",
      width: "25%",
      render: (text, record) => (
        <span
          style={{
            color: 'var(--hust-color)',
            cursor: 'pointer',
            textDecoration: 'underline',
          }}
          onClick={() => handleProblemStatisticClick(record.problemId, record.name)}
        >
          {text}
        </span>
      ),
    },
    {
      title: "Độ khó",
      dataIndex: "difficulty",
      key: "difficulty",
      width: "10%",
      align: "center",
      render: (text) => {
        let color = '#d9d9d9';
        if (text === 'Easy') color = '#52c41a';
        else if (text === 'Medium') color = '#faad14';
        else if (text === 'Hard') color = '#d4380d';
        return <span style={{ color, fontWeight: 500 }}>{text}</span>;
      },
    },
    {
      title: "Tổng bài nộp",
      dataIndex: "totalSubmissions",
      key: "totalSubmissions",
      width: "12%",
      align: "center",
    },
    {
      title: "AC",
      dataIndex: "acceptedCount",
      key: "acceptedCount",
      width: "8%",
      align: "center",
      render: (text) => <span style={{ color: '#52c41a' }}>{text}</span>,
    },
    {
      title: "Tỉ lệ AC",
      dataIndex: "acRate",
      key: "acRate",
      width: "10%",
      align: "center",
      render: (text) => <span style={{ color: '#1890ff', fontWeight: 500 }}>{text}%</span>,
    }
  ];

  if (loading) {
    return <Spin tip="Đang tải dữ liệu..." />;
  }

  return (
    <div className='topic-contest-detail'>
      <div className='topic-contest-detail-header'>
        <Breadcrumb
          items={[
            {
              title: (
                <a
                  onClick={() => navigate(appPath.topicContest)}
                  style={{
                    color: 'var(--hust-color)',
                    fontSize: '30px',
                    fontWeight: 600
                  }}
                >
                  Danh sách chủ đề
                </a>
              )
            },
            {
              title: (
                <span
                  style={{
                    color: 'var(--hust-color)',
                    fontSize: '30px',
                    fontWeight: 600
                  }}
                >
                  {topicContest?.name || 'Chi tiết'}
                </span>
              )
            }
          ]}
        />
      </div>
      <Card
        title={`Danh sách các đề thi: ${topicContest?.name || 'N/A'}`}
        extra={
          <Button
            type="default"
            icon={<ArrowLeftOutlined />}
            onClick={() => navigate(appPath.topicContest)}
          >
            Quay lại
          </Button>
        }
        className='topic-detail-card'
      >
        <div className='problems-section'>
          {problems && problems.length > 0 ? (
            <Table
              columns={columns}
              dataSource={problems}
              rowKey="key"
              className="problems-table"
              pagination={{
                pageSize: 10,
                showSizeChanger: true,
                pageSizeOptions: ['5', '10', '20'],
                showTotal: (total, range) => (
                  <span>
                    <strong>
                      {range[0]}-{range[1]}
                    </strong>{" "}
                    trong <strong>{total}</strong> đề thi
                  </span>
                ),
              }}
            />
          ) : (
            <Empty description="Chưa có đề thi nào" />
          )}
        </div>
      </Card>
    </div>
  );
}
