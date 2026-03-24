import React, { useState, useMemo } from 'react';
import {
  Search,
  ChevronLeft,
  ChevronRight,
  Eye,
  Grid3X3,
  List,
  X,
  Check,
  Filter,
  Download
} from 'lucide-react';
import * as XLSX from 'xlsx';
import './TestCase.scss';

const TestCase = ({ testCases, onRemoveTestCase }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [viewMode, setViewMode] = useState('list');
  const [selectedTestCase, setSelectedTestCase] = useState(null);
  const [showFilter, setShowFilter] = useState(false);
  const [filterStatus, setFilterStatus] = useState('all');

  const filteredTestCases = useMemo(() => {
    let filtered = testCases.filter(tc =>
      tc.input?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      tc.expected_output?.toString().toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (filterStatus !== 'all') {
      filtered = filtered.filter(tc => tc.status === filterStatus);
    }

    return filtered;
  }, [testCases, searchTerm, filterStatus]);

  const totalPages = Math.ceil(filteredTestCases.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentTestCases = filteredTestCases.slice(startIndex, startIndex + itemsPerPage);

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const handleItemsPerPageChange = (value) => {
    setItemsPerPage(value);
    setCurrentPage(1);
  };

  const viewTestCase = (testCase, index) => {
    setSelectedTestCase({ ...testCase, index: startIndex + index });
  };

  const exportTestCases = () => {
    const exportData = filteredTestCases.map((tc, index) => ({
      STT: index + 1,
      Input: tc.input,
      Output: tc.expected_output,
      Status: tc.status || '',
      Explanation: tc.explanation || ''
    }));

    const worksheet = XLSX.utils.json_to_sheet(exportData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Test Cases');
    XLSX.writeFile(workbook, 'test-cases.xlsx');
  };

  return (
    <div className="test-cases-viewer">
      {/* Header */}
      <div className="viewer-header">
        <div className="header-left">
          <div className="test-count">
            <Check className="text-green-500" size={18} />
            <span>Test Cases ({filteredTestCases.length}/{testCases.length})</span>
          </div>
        </div>

        <div className="header-right">
          <button
            onClick={() => setShowFilter(!showFilter)}
            className={`btn-icon ${showFilter ? 'active' : ''}`}
          >
            <Filter size={16} />
          </button>
          <button onClick={exportTestCases} className="btn-icon" title="Export Excel">
            <Download size={16} />
          </button>
          <div className="view-modes">
            <button
              onClick={() => setViewMode('list')}
              className={`btn-icon ${viewMode === 'list' ? 'active' : ''}`}
            >
              <List size={16} />
            </button>
            <button
              onClick={() => setViewMode('grid')}
              className={`btn-icon ${viewMode === 'grid' ? 'active' : ''}`}
            >
              <Grid3X3 size={16} />
            </button>
          </div>
        </div>
      </div>

      {/* Filters */}
      {showFilter && (
        <div className="filters-panel">
          <div className="search-box">
            <Search size={16} />
            <input
              type="text"
              placeholder="Tìm kiếm test cases..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          <select
            value={filterStatus}
            onChange={(e) => setFilterStatus(e.target.value)}
            className="filter-select"
          >
            <option value="all">Tất cả</option>
            <option value="passed">Passed</option>
            <option value="failed">Failed</option>
          </select>
        </div>
      )}

      {/* Test Cases Display */}
      <div className={`test-cases-content ${viewMode}`}>
        {currentTestCases.map((tc, idx) => (
          <div key={startIndex + idx} className="test-case-card">
            <div className="test-case-header">
              <span className="test-case-number">#{startIndex + idx + 1}</span>
              <div className="test-case-actions">
                <button
                  onClick={() => viewTestCase(tc, idx)}
                  className="btn-action view"
                  title="Xem chi tiết"
                >
                  <Eye size={14} />
                </button>
                <button
                  onClick={() => onRemoveTestCase(startIndex + idx)}
                  className="btn-action remove"
                  title="Xóa"
                >
                  <X size={14} />
                </button>
              </div>
            </div>

            <div className="test-case-preview">
              <div className="input-preview">
                <span className="label">Input:</span>
                <code className="code-preview">
                  {tc.input.length > 50 ? `${tc.input.substring(0, 50)}...` : tc.input}
                </code>
              </div>
              <div className="output-preview">
                <span className="label">Expected Output:</span>
                <code className="code-preview">
                  {tc.expected_output.toString().length > 50
                    ? `${tc.expected_output.toString().substring(0, 50)}...`
                    : tc.expected_output}
                </code>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Pagination */}
      <div className="pagination">
        <div className="pagination-info">
          <span>
            Hiển thị {startIndex + 1}-{Math.min(startIndex + itemsPerPage, filteredTestCases.length)} của {filteredTestCases.length}
          </span>
          <select
            value={itemsPerPage}
            onChange={(e) => handleItemsPerPageChange(Number(e.target.value))}
            className="items-per-page"
          >
            <option value={5}>5/trang</option>
            <option value={10}>10/trang</option>
            <option value={20}>20/trang</option>
            <option value={50}>50/trang</option>
          </select>
        </div>

        <div className="pagination-controls">
          <button
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage === 1}
            className="btn-page"
          >
            <ChevronLeft size={16} />
          </button>

          {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
            let pageNum;
            if (totalPages <= 5) {
              pageNum = i + 1;
            } else if (currentPage <= 3) {
              pageNum = i + 1;
            } else if (currentPage >= totalPages - 2) {
              pageNum = totalPages - 4 + i;
            } else {
              pageNum = currentPage - 2 + i;
            }

            return (
              <button
                key={pageNum}
                onClick={() => handlePageChange(pageNum)}
                className={`btn-page ${currentPage === pageNum ? 'active' : ''}`}
              >
                {pageNum}
              </button>
            );
          })}

          <button
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={currentPage === totalPages}
            className="btn-page"
          >
            <ChevronRight size={16} />
          </button>
        </div>
      </div>

      {/* Modal */}
      {selectedTestCase && (
        <div className="modal-overlay" onClick={() => setSelectedTestCase(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Test Case #{selectedTestCase.index + 1}</h3>
              <button
                onClick={() => setSelectedTestCase(null)}
                className="btn-close"
              >
                <X size={20} />
              </button>
            </div>
            <div className="modal-body">
              <div className="test-case-detail">
                <div className="detail-section">
                  <label>Input:</label>
                  <pre className="code-block">{selectedTestCase.input}</pre>
                </div>
                <div className="detail-section">
                  <label>Expected Output:</label>
                  <pre className="code-block">{selectedTestCase.expected_output}</pre>
                </div>
                {selectedTestCase.explanation && (
                  <div className="detail-section">
                    <label>Giải thích:</label>
                    <p className="explanation">{selectedTestCase.explanation}</p>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TestCase;
