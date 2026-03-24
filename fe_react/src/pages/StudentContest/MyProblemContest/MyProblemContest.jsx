import React, { useState, useEffect } from 'react';
import { Space, Table, Button, message } from 'antd';
import { ReloadOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import { appPath } from '../../../config/appPath';
import './MyProblemContest.scss';
import SearchFilter from '../../../components/SearchFilter/SearchFilter';
import { getSubmissionByStudent } from '../../../services/studentContestService';

const VERDICT_CONFIG = {
  ACCEPTED:             { label: 'Accepted',       color: '#52c41a' },
  WRONG_ANSWER:         { label: 'Wrong Answer',   color: '#faad14' },
  TIME_LIMIT_EXCEEDED:  { label: 'Time Limit',     color: '#2314fa' },
  COMPILE_ERROR:        { label: 'Compiler Error', color: '#d9d9d9' },
  COMPILATION_ERROR:    { label: 'Compiler Error', color: '#d9d9d9' }, 
  RUNTIME_ERROR:        { label: 'Runtime Error',  color: '#ff7a45' },
  MEMORY_LIMIT_EXCEEDED:{ label: 'Memory Limit',   color: '#1890ff' },
};

const renderCustomLabel = ({ cx, cy, midAngle, outerRadius, percent }) => {
  if (percent < 0.03) return null; 
  const RADIAN = Math.PI / 180;
  const radius = outerRadius + 24;
  const x = cx + radius * Math.cos(-midAngle * RADIAN);
  const y = cy + radius * Math.sin(-midAngle * RADIAN);
  return (
    <text
      x={x}
      y={y}
      textAnchor={x > cx ? 'start' : 'end'}
      dominantBaseline="central"
      className="my-problem-contest-chart-slice-label"
    >
      {`${(percent * 100).toFixed(1)}%`}
    </text>
  );
};

// ── Custom tooltip ────────────────────────────────────────────────────────
const CustomTooltip = ({ active, payload }) => {
  if (!active || !payload?.length) return null;
  const { name, value, payload: { color } } = payload[0];
  return (
    <div
      className="my-problem-contest-tooltip"
      style={{ borderColor: color }}
    >
      <span
        className="my-problem-contest-tooltip-label"
        style={{ color }}
      >
        {name}
      </span>
      <span className="my-problem-contest-tooltip-value">
        {value} bài
      </span>
    </div>
  );
};

export default function MyProblemContest() {
  const [submissions, setSubmissions]             = useState([]);
  const [filteredSubmissions, setFilteredSubmissions] = useState([]);
  const [loading, setLoading]                     = useState(false);
  const [page, setPage]                           = useState(1);
  const [pageSize, setPageSize]                   = useState(10);
  const navigate = useNavigate();

  useEffect(() => { fetchMySubmissions(); }, []);

  const fetchMySubmissions = () => {
    setLoading(true);
    getSubmissionByStudent(
      {},
      (response) => {
        let data = response?.data || [];
        data.sort((a, b) => new Date(b.submittedAt) - new Date(a.submittedAt));

        const mapped = data.map((item, index) => {
          const cfg = VERDICT_CONFIG[item.verdict] || { label: item.verdict, color: '#d4380d' };
          return {
            id:            item.submissionId,
            key:           item.submissionId.toString(),
            ordinal:       index + 1,
            topicName:     item.topicName,
            problemName:   item.problemNameContest,
            status:        cfg.label,
            statusColor:   cfg.color,
            verdict:       item.verdict,
            executionTime: item.runtimeMs  ? `${item.runtimeMs}ms`                       : '-',
            memoryUsage:   item.memoryKb   ? `${(item.memoryKb / 1024).toFixed(2)}MB`    : '-',
            testCasePassed:`${item.passedTestcases}/${item.totalTestcases}`,
            submittedAt:   item.submittedAt,
            language:      item.language,
            problemId:     item.problemId || item.problemContestId,
          };
        });

        setSubmissions(mapped);
        setFilteredSubmissions(mapped);
        setLoading(false);
      },
      () => {
        message.error('Lỗi lấy danh sách bài nộp!');
        setLoading(false);
      }
    );
  };

  const pieData = React.useMemo(() => {
    const counts = {};
    submissions.forEach(({ status, statusColor }) => {
      if (!counts[status]) counts[status] = { count: 0, color: statusColor };
      counts[status].count += 1;
    });
    return Object.entries(counts)
      .map(([name, { count, color }]) => ({ name, value: count, color }))
      .sort((a, b) => b.value - a.value);
  }, [submissions]);

  const columns = [
    {
      title: 'Số thứ tự', dataIndex: 'ordinal', key: 'ordinal',
      width: '8%', align: 'center',
    },
    { title: 'Tên chủ đề',  dataIndex: 'topicName',   key: 'topicName',   width: '18%' },
    { title: 'Tên đề thi',  dataIndex: 'problemName', key: 'problemName', width: '20%' },
    {
      title: 'Trạng thái', dataIndex: 'status', key: 'status',
      width: '13%', align: 'center',
      render: (text, record) => (
        <span style={{ color: record.statusColor, fontWeight: 500 }}>{text}</span>
      ),
    },
    {
      title: 'Ngày nộp', dataIndex: 'submittedAt', key: 'submittedAt',
      width: '14%', align: 'center',
      render: (text) => new Date(text).toLocaleString('vi-VN'),
      sorter: (a, b) => new Date(b.submittedAt) - new Date(a.submittedAt),
    },
    { title: 'Time Excu',   dataIndex: 'executionTime', key: 'executionTime', width: '11%', align: 'center' },
    { title: 'Memory Excu', dataIndex: 'memoryUsage',   key: 'memoryUsage',   width: '12%', align: 'center' },
    {
      title: 'Thao tác', key: 'action', width: '6%', align: 'center',
      render: (_, record) => (
        <Space size="small">
          <Button type="link" size="small" onClick={() => handleViewDetail(record.id)}>Xem</Button>
        </Space>
      ),
    },
  ];

  const handleViewDetail = (submissionId) => {
    const submission = submissions.find(s => s.id === submissionId);
    console.log(submission);
    if (submission && submission.problemId) {
      navigate(`${appPath.studentProblemContestDetail}/${submission.problemId}?submissionId=${submissionId}`);
    } else {
      message.error('Không tìm thấy thông tin bài tập');
    }
  };

  const handleSearch = (value) => {
    if (!value) { setFilteredSubmissions(submissions); setPage(1); return; }
    const v = value.toLowerCase();
    setFilteredSubmissions(
      submissions.filter(s =>
        s.topicName.toLowerCase().includes(v) ||
        s.problemName.toLowerCase().includes(v) ||
        s.status.toLowerCase().includes(v)
      )
    );
    setPage(1);
  };

  const handlePageChange = (newPage, newPageSize) => { setPage(newPage); setPageSize(newPageSize); };

  return (
    <div className="my-problem-contest">
      <div className="my-problem-contest-header">
        <p>Danh sách các bài nộp</p>
      </div>

      <div className="search-filter-button">
        <SearchFilter
          placeholder="Tìm kiếm theo chủ đề, đề thi hoặc trạng thái"
          onSearch={handleSearch}
        />
        <Button
          type="primary"
          icon={<ReloadOutlined />}
          onClick={fetchMySubmissions}
          style={{ marginLeft: 10 }}
        >
          Làm mới
        </Button>
      </div>

      {/* ── Table ── */}
      <div className="my-problem-contest-wrapper">
        <Table
          columns={columns}
          dataSource={filteredSubmissions}
          rowKey="key"
          className="my-problem-contest-table"
          loading={loading}
          pagination={{
            current: page,
            pageSize,
            total: filteredSubmissions.length,
            onChange: handlePageChange,
            showSizeChanger: true,
            pageSizeOptions: ['5', '10', '20'],
            showTotal: (total, range) => (
              <span>
                <strong>{range[0]}-{range[1]}</strong> trong <strong>{total}</strong> bài nộp
              </span>
            ),
          }}
        />
      </div>
      {submissions.length > 0 && (
        <div className="my-problem-contest-chart">
          <h3 className="my-problem-contest-chart-title">
            Thống kê kết quả bài nộp
          </h3>

          <ResponsiveContainer width="100%" height={440}>
            <PieChart>
              <Pie
                data={pieData}
                cx="50%"
                cy="50%"
                outerRadius={180}
                innerRadius={52}      
                dataKey="value"
                labelLine={false}
                label={renderCustomLabel}
                strokeWidth={2}
                stroke="#fff"
              >
                {pieData.map((entry) => (
                  <Cell key={entry.name} fill={entry.color} />
                ))}
              </Pie>

              <Tooltip content={<CustomTooltip />} />

              <Legend
                iconType="circle"
                iconSize={10}
                formatter={(value, entry) => (
                  <span className="my-problem-contest-chart-legend-label">
                    {value}
                    <span className="my-problem-contest-chart-legend-count">
                      ({entry.payload.value})
                    </span>
                  </span>
                )}
              />

              <text
                x="50%"
                y="50%"
                textAnchor="middle"
                dominantBaseline="central"
                className="my-problem-contest-chart-center-label"
              >
                {submissions.length} bài
              </text>
            </PieChart>
          </ResponsiveContainer>
        </div>
      )}
    </div>
  );
}