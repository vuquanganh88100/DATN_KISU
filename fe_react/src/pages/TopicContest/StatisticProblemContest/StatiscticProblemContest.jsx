import React, { useEffect, useMemo, useState } from 'react';
import { Button, message, Table, Card, Breadcrumb } from 'antd';
import { ArrowLeftOutlined, ReloadOutlined } from '@ant-design/icons';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import './StatiscticProblemContest.scss';
import SearchFilter from '../../../components/SearchFilter/SearchFilter';
import { getSubmissionsByProblem } from '../../../services/problemContestService';
import { appPath } from '../../../config/appPath';

const VERDICT_CONFIG = {
  ACCEPTED:              { label: 'Accepted',       color: '#52c41a' },
  WRONG_ANSWER:          { label: 'Wrong Answer',   color: '#faad14' },
  TIME_LIMIT_EXCEEDED:   { label: 'Time Limit',     color: '#2314fa' },
  COMPILE_ERROR:         { label: 'Compiler Error', color: '#d9d9d9' },
  COMPILATION_ERROR:     { label: 'Compiler Error', color: '#d9d9d9' },
  RUNTIME_ERROR:         { label: 'Runtime Error',  color: '#ff7a45' },
  MEMORY_LIMIT_EXCEEDED: { label: 'Memory Limit',   color: '#1890ff' },
};

const SEARCH_FIELDS = ['status', 'topicName', 'problemNameContest', 'studentName', 'language'];

const renderCustomLabel = ({ cx, cy, midAngle, outerRadius, percent }) => {
  if (percent < 0.03) return null;
  const r = Math.PI / 180;
  const radius = outerRadius + 24;
  const x = cx + radius * Math.cos(-midAngle * r);
  const y = cy + radius * Math.sin(-midAngle * r);
  return (
    <text x={x} y={y} textAnchor={x > cx ? 'start' : 'end'} dominantBaseline="central"
      className="statistic-problem-contest-chart-slice-label">
      {`${(percent * 100).toFixed(1)}%`}
    </text>
  );
};

const CustomTooltip = ({ active, payload }) => {
  if (!active || !payload?.length) return null;
  const { name, value, payload: { color } } = payload[0];
  return (
    <div className="statistic-problem-contest-tooltip" style={{ borderColor: color }}>
      <span className="statistic-problem-contest-tooltip-label" style={{ color }}>{name}</span>
      <span className="statistic-problem-contest-tooltip-value">{value} bài</span>
    </div>
  );
};

const COLUMNS = [
  { title: 'Số thứ tự',   dataIndex: 'ordinal',           key: 'ordinal',           width: '8%',  align: 'center' },
  { title: 'Họ tên',  dataIndex: 'studentName',         key: 'studentName',         width: '12%', align: 'left' },
  { title: 'Tên đề thi',  dataIndex: 'problemNameContest', key: 'problemNameContest', width: '15%', align: 'left' },
  {
    title: 'Trạng thái', dataIndex: 'status', key: 'status', width: '12%', align: 'center',
    render: (text, { statusColor }) => <span style={{ color: statusColor, fontWeight: 500 }}>{text}</span>,
  },
  {
    title: 'Ngày nộp', dataIndex: 'submittedAt', key: 'submittedAt', width: '15%', align: 'center',
    render: (text) => new Date(text).toLocaleString('vi-VN'),
    sorter: (a, b) => new Date(b.submittedAt) - new Date(a.submittedAt),
  },
  { title: 'Time Excu',   dataIndex: 'executionTime', key: 'executionTime', width: '10%', align: 'center' },
  { title: 'Memory Excu', dataIndex: 'memoryUsage',   key: 'memoryUsage',   width: '10%', align: 'center' },
  {
    title: 'Thao tác', key: 'action', width: '8%', align: 'center',
    render: (_, { id }) => (
      <Button type="link" size="small" onClick={() => console.log('View submission:', id)}>Xem</Button>
    ),
  },
];

const mapSubmission = (item, index) => {
  const cfg = VERDICT_CONFIG[item.verdict] || { label: item.verdict, color: '#d4380d' };
  return {
    id:                 item.submissionId,
    key:                item.submissionId.toString(),
    ordinal:            index + 1,
    topicName:          item.topicName,
    problemNameContest: item.problemNameContest,
    status:             cfg.label,
    statusColor:        cfg.color,
    verdict:            item.verdict,
    executionTime:      item.runtimeMs ? `${item.runtimeMs.toFixed(3)}ms` : '-',
    memoryUsage:        item.memoryKb  ? `${(item.memoryKb / 1024).toFixed(2)}MB` : '-',
    testCasePassed:     `${item.passedTestcases}/${item.totalTestcases}`,
    submittedAt:        item.submittedAt,
    language:           item.language,
    studentName:        item.studentName,
  };
};

export default function StatiscticProblemContest() {
  const { problemId } = useParams();
  const { state } = useLocation();
  const navigate = useNavigate();

  const problemNameFromState = state?.problemName || '';

  const [submissions, setSubmissions] = useState([]);
  const [filteredSubmissions, setFilteredSubmissions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  useEffect(() => {
    if (!problemNameFromState) {
      message.error('Thiếu thông tin đề thi, quay lại màn trước.');
      navigate(-1);
      return;
    }
    fetchSubmissions();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [problemNameFromState]);

  const fetchSubmissions = () => {
    setLoading(true);
    getSubmissionsByProblem(
      problemId, {},
      (response) => {
  console.log('full response:', response);
  console.log('response.data:', response?.data);
  console.log('response.data.data:', response?.data?.data);
        const mapped = (response || [])
          .sort((a, b) => new Date(b.submittedAt) - new Date(a.submittedAt))
          .map(mapSubmission);
        setSubmissions(mapped);
        setFilteredSubmissions(mapped);
        setLoading(false);
      },
      () => { message.error('Lỗi lấy danh sách bài nộp!'); setLoading(false); }
    );
  };

  const pieData = useMemo(() => {
    const counts = {};
    submissions.forEach(({ status, statusColor }) => {
      counts[status] ??= { count: 0, color: statusColor };
      counts[status].count += 1;
    });
    return Object.entries(counts)
      .map(([name, { count, color }]) => ({ name, value: count, color }))
      .sort((a, b) => b.value - a.value);
  }, [submissions]);

  const handleSearch = (value) => {
    if (!value) { setFilteredSubmissions(submissions); setPage(1); return; }
    const v = value.toLowerCase();
    setFilteredSubmissions(
      submissions.filter((s) => SEARCH_FIELDS.some((f) => s[f]?.toLowerCase().includes(v)))
    );
    setPage(1);
  };

  return (
    <div className="statistic-problem-contest">
      <div className="statistic-problem-contest-header">
        <Breadcrumb
          items={[
            {
              title: (
                <a
                  onClick={() => navigate(appPath.topicContest)}
                  style={{ color: 'var(--hust-color)', fontSize: '30px', fontWeight: 600 }}
                >
                  Danh sách chủ đề
                </a>
              )
            },
            {
              title: (
                <span style={{ color: 'var(--hust-color)', fontSize: '30px', fontWeight: 600 }}>
                  Thống kê bài nộp: {problemNameFromState || `Đề thi #${problemId}`}
                </span>
              )
            }
          ]}
        />
      </div>

      <div className="search-filter-button">
        <SearchFilter placeholder="Tìm theo trạng thái hoặc ngôn ngữ" onSearch={handleSearch} />
      </div>

      <Card
        className="statistic-problem-contest-wrapper"
        title={`Lịch sử nộp bài: ${problemNameFromState}`}
        extra={
          <Button type="default" icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)}>
            Quay lại
          </Button>
        }
      >
        <Table
          columns={COLUMNS}
          dataSource={filteredSubmissions}
          rowKey="key"
          className="statistic-problem-contest-table"
          loading={loading}
          pagination={{
            current: page,
            pageSize,
            total: filteredSubmissions.length,
            onChange: (p, ps) => {
              setPage(p);
              setPageSize(ps);
            },
            showSizeChanger: true,
            pageSizeOptions: ['5', '10', '20'],
            showTotal: (total, [from, to]) => (
              <span>
                <strong>{from}-{to}</strong> trong <strong>{total}</strong> lần nộp
              </span>
            ),
          }}
        />
      </Card>

      {submissions.length > 0 && (
        <Card className="statistic-problem-contest-chart">
          <h3 className="statistic-problem-contest-chart-title">Biểu đồ thống kê kết quả</h3>
          <ResponsiveContainer width="100%" height={440}>
            <PieChart>
              <Pie data={pieData} cx="50%" cy="50%" outerRadius={180} innerRadius={52}
                dataKey="value" labelLine={false} label={renderCustomLabel} strokeWidth={2} stroke="#fff">
                {pieData.map((entry) => <Cell key={entry.name} fill={entry.color} />)}
              </Pie>
              <Tooltip content={<CustomTooltip />} />
              <Legend iconType="circle" iconSize={10}
                formatter={(value, { payload }) => (
                  <span className="statistic-problem-contest-chart-legend-label">
                    {value}
                    <span className="statistic-problem-contest-chart-legend-count">({payload.value})</span>
                  </span>
                )}
              />
              <text x="50%" y="50%" textAnchor="middle" dominantBaseline="central"
                className="statistic-problem-contest-chart-center-label">
                {submissions.length} lần nộp
              </text>
            </PieChart>
          </ResponsiveContainer>
        </Card>
      )}
    </div>
  );
}