import React from 'react';
import { Card, Button, Empty, Spin, Space } from 'antd';
import { LikeOutlined, MessageOutlined } from '@ant-design/icons';

const LANGUAGE_MAP = {
  62: 'Java',
  71: 'Python',
  72: 'JavaScript',
  73: 'TypeScript',
  74: 'C++',
  75: 'C',
  76: 'C#',
  77: 'Go',
  78: 'Rust',
};

const SolutionList = ({ solutions, loading, onSelectSolution, onPostClick }) => {
  return (
    <div className="solution-list-view">
      <div className="solution-toolbar">
        <h2>Solutions</h2>
        <Button
          type="primary"
          size="large"
          onClick={onPostClick}
        >
          Post Solution
        </Button>
      </div>

      <Spin spinning={loading}>
        {solutions && solutions.length > 0 ? (
          <div className="solutions-list">
            {solutions.map((solution) => (
              <Card
                className="solution-card"
                key={solution.solutionId}
                onClick={() => onSelectSolution(solution)}
                hoverable
              >
                <div className="solution-header">
                  <div className="solution-title-section">
                    <h3 className="solution-title">{solution.title}</h3>
                    <span className="language-tag">
                      {LANGUAGE_MAP[solution.languageId] || `Language ${solution.languageId}`}
                    </span>
                  </div>
                </div>

                <div className="solution-meta-compact">
                  <span className="complexity">
                    <strong>Time:</strong> {solution.timeComplex || 'N/A'}
                  </span>
                  <span className="complexity">
                    <strong>Space:</strong> {solution.spaceComplex || 'N/A'}
                  </span>
                </div>

                <div className="solution-author">
                  <span className="author-info">
                    User #{solution.createdBy}
                    <br />
                    <small>{new Date(solution.createdAt).toLocaleDateString()}</small>
                  </span>
                </div>

                <div className="solution-actions-compact">
                  <Button
                    type="text"
                    size="small"
                    icon={<LikeOutlined />}
                  >
                    Like
                  </Button>
                  <Button
                    type="text"
                    size="small"
                    icon={<MessageOutlined />}
                  >
                    {(solution.comments || []).length} Comments
                  </Button>
                </div>
              </Card>
            ))}
          </div>
        ) : (
          <Empty description="No solutions yet. Be the first to share!" />
        )}
      </Spin>
    </div>
  );
};

export default SolutionList;
