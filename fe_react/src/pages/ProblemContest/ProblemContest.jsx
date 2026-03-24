import React, { useEffect, useState } from 'react';
import { FileSpreadsheet, Sparkles, Save, Upload, Play, X } from 'lucide-react';
import './ProblemContest.scss';
import TestCase from '../TestCase/TestCase';
import { generateInput, generateTestCase } from '../../services/testCaseService';
import { getTopicContests } from '../../services/topicContestService';
import { createProblemContest } from '../../services/problemContestService';

const DIFFICULTY_LEVELS = [
  { value: 'easy', label: 'Dễ', color: '#52c41a' },
  { value: 'medium', label: 'Trung bình', color: '#faad14' },
  { value: 'hard', label: 'Khó', color: '#ff4d4f' },
];

const LANGUAGES = [
  { id: 49, label: 'C (GCC)' },
  { id: 50, label: 'C++ (GCC)' },
  { id: 51, label: 'C# (.NET)' },
  { id: 62, label: 'Java (OpenJDK)' },
  { id: 63, label: 'JavaScript (Node.js)' },
  { id: 71, label: 'Python' },
  { id: 72, label: 'Rust' },
  { id: 73, label: 'Go' },
  { id: 64, label: 'Bash' }
];

const INITIAL_FORM = {
  title: '',
  description: '',
  inputFormat: '',
  outputFormat: '',
  constraints: '',
  topic: '',
  level: '',
};

function ProblemContest() {
  const [form, setForm] = useState(INITIAL_FORM);
  const [topics, setTopics] = useState([]);
  const [excelFile, setExcelFile] = useState(null);
  const [aiGenerating, setAiGenerating] = useState(false);
  const [aiInputs, setAiInputs] = useState([]); // Danh sách input sinh ra từ AI
  const [editingInputs, setEditingInputs] = useState([]); // Input sau khi user chỉnh sửa
  const [sourceCode, setSourceCode] = useState(''); // Source code từ user
  const [languageId, setLanguageId] = useState(54); // Language ID (mặc định C++ GCC)
  const [isRunning, setIsRunning] = useState(false); // Đang chạy test
  const [testCases, setTestCases] = useState([]); // Test cases hoàn chỉnh (input + output)
  const [notification, setNotification] = useState('');
  const [step, setStep] = useState(1); // Step: 1=sinh input, 2=nhập code, 3=chạy test

  useEffect(() => {
    getTopicContests(
      {},
      (res) => setTopics(Array.isArray(res) ? res : res?.data || []),
      (err) => console.error('Lỗi khi tải chủ đề:', err)
    );
  }, []);

  const handleChange = (e) => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const showNotification = (msg) => {
    setNotification(msg);
    setTimeout(() => setNotification(''), 3000);
  };

  const parseJsonLines = (data) => {
    if (!data) return [];
    if (Array.isArray(data)) return data;
    if (typeof data === 'object') return [data];
    
    try {
      return data.trim().split('\n')
        .filter(line => line.trim())
        .map(line => {
          try { return JSON.parse(line); } 
          catch { return null; }
        })
        .filter(Boolean);
    } catch {
      return [];
    }
  };

  // Chỉ lấy input từ API
  const normalizeAiInputs = (data = []) => {
    if (!Array.isArray(data)) return [];
    return data.map((item, i) => ({
      input: item.input || '',
      id: i,
    }));
  };

  const handleGenerateAI = async () => {
    setAiGenerating(true);
    try {
      await generateInput(
        form,
        (res) => {
          console.log('🔍 API Response:', res); // Debug log
          
          // Parse response - có thể là array hoặc JSON lines string
          let inputList = [];
          if (Array.isArray(res)) {
            inputList = res;
          } else if (res?.data) {
            inputList = Array.isArray(res.data) ? res.data : parseJsonLines(res.data);
          } else if (typeof res === 'string') {
            inputList = parseJsonLines(res);
          } else {
            inputList = [];
          }
          
          console.log('📋 Parsed Input List:', inputList); // Debug log
          
          const formatted = normalizeAiInputs(inputList);
          console.log('✨ Formatted Inputs:', formatted); // Debug log
          
          setAiInputs(formatted);
          setEditingInputs(formatted.map(tc => ({ ...tc })));
          
          if (formatted.length > 0) {
            setStep(2); // Chuyển sang bước kiểm tra input
          }
          
          showNotification(
            formatted.length 
              ? `✅ Đã sinh thành công ${formatted.length} input từ AI! Vui lòng kiểm tra xem có đúng constraint không.`
              : '⚠️ Không có input nào được sinh ra.'
          );
        },
        (err) => {
          console.error('❌ Lỗi API:', err);
          showNotification('❌ Lỗi khi sinh input bằng AI.');
        }
      );
    } catch (err) {
      console.error('❌ Exception:', err);
      showNotification('❌ Đã xảy ra lỗi.');
    } finally {
      setAiGenerating(false);
    }
  };

  // Update input sau khi user chỉnh sửa
  const handleEditInput = (index, newInput) => {
    setEditingInputs(prev => {
      const updated = [...prev];
      updated[index].input = newInput;
      return updated;
    });
  };

  // Xóa input
  const handleDeleteInput = (index) => {
    setEditingInputs(prev => prev.filter((_, i) => i !== index));
  };

  // Chuyển sang bước nhập source code
  const handleProceedToCode = () => {
    if (editingInputs.length === 0) {
      alert('Vui lòng giữ lại ít nhất 1 input.');
      return;
    }
    setStep(3);
    showNotification('📝 Vui lòng nhập source code của bạn');
  };

  // Chạy test cases - gọi API generateTestCase với source code
  const handleRunTests = async () => {
    if (!sourceCode.trim()) {
      alert('Vui lòng nhập source code.');
      return;
    }

    setIsRunning(true);
    try {
      // Format payload theo yêu cầu API (FormData)
      // Mảng các object: { input, sourceCode, languageId }
      const payload = editingInputs.map(tc => ({
        input: tc.input,
        sourceCode: sourceCode,
        languageId: Number(languageId),
      }));

      await generateTestCase(
        payload,
        (res) => {
          const testCasesFromAPI = Array.isArray(res) ? res : res?.data || [];
          
          const formatted = testCasesFromAPI.map((tc, i) => ({
            input: tc.input || '',
            expected_output: tc.expectedOutput || '',
            public: tc.public || false,
            id: i,
          }));

          setTestCases(formatted);
          setStep(4); // Chuyển sang xem kết quả
          showNotification('✅ Đã chạy test thành công! Kiểm tra kết quả dưới đây.');
        },
        (err) => {
          console.error('Lỗi:', err);
          showNotification('❌ Lỗi khi chạy test.');
        }
      );
    } catch (err) {
      console.error('Lỗi:', err);
      showNotification('❌ Đã xảy ra lỗi.');
    } finally {
      setIsRunning(false);
    }
  };

  const handleSubmit = () => {
    const { title, description, inputFormat, outputFormat, constraints, topic, level } = form;

    if (!title || !description || !inputFormat || !outputFormat || !constraints || !topic || !level) {
      return alert('Vui lòng điền đầy đủ thông tin.');
    }

    if (!testCases.length) {
      return alert('Vui lòng chạy test cases trước khi lưu.');
    }

    const payload = {
      ...form,
      teacherId: 1,
      topicContestId: Number(topic),
      testCases: testCases.map(tc => ({
        input: tc.input,
        expectedOutput: tc.expected_output,
        public: tc.public || false,
      })),
    };

    createProblemContest(
      payload,
      () => {
        alert('✅ Đã lưu Problem Contest thành công!');
        // Reset all states
        setForm(INITIAL_FORM);
        setTestCases([]);
        setAiInputs([]);
        setEditingInputs([]);
        setSourceCode('');
        setStep(1);
      },
      (err) => {
        console.error('❌ Lỗi:', err);
        alert('❌ Lưu thất bại!');
      }
    );
  };

  return (
    <div className="problem-contest">
      <div className="pc-header">
        <h1>Tạo Problem Contest</h1>
      </div>

      {notification && (
        <div className="notification">
          {notification}
        </div>
      )}

      <div className="pc-form">
        {/* Thông tin cơ bản */}
        <section className="form-section">
          <h3>Thông tin cơ bản</h3>

          <div className="form-field">
            <label>Tiêu đề bài toán</label>
            <input
              name="title"
              value={form.title}
              onChange={handleChange}
              placeholder="Nhập tiêu đề bài toán..."
            />
          </div>

          <div className="form-field">
            <label>Mô tả bài toán</label>
            <textarea
              name="description"
              value={form.description}
              onChange={handleChange}
              rows={5}
              placeholder="Mô tả chi tiết bài toán..."
            />
          </div>

          <div className="form-row">
            <div className="form-field">
              <label>Định dạng Input</label>
              <textarea
                name="inputFormat"
                value={form.inputFormat}
                onChange={handleChange}
                rows={3}
                placeholder="Mô tả định dạng dữ liệu đầu vào..."
              />
            </div>
            <div className="form-field">
              <label>Định dạng Output</label>
              <textarea
                name="outputFormat"
                value={form.outputFormat}
                onChange={handleChange}
                rows={3}
                placeholder="Mô tả định dạng kết quả đầu ra..."
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-field">
              <label>Ràng buộc (Constraints)</label>
              <textarea
                name="constraints"
                value={form.constraints}
                onChange={handleChange}
                rows={3}
                placeholder="Nhập các ràng buộc..."
              />
            </div>
            <div className="form-field">
              <label>Chủ đề</label>
              <select name="topic" value={form.topic} onChange={handleChange}>
                <option value="">Chọn chủ đề</option>
                {topics.map(t => (
                  <option key={t.id} value={t.id}>{t.name}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-field">
            <label>Mức độ khó</label>
            <select name="level" value={form.level} onChange={handleChange}>
              <option value="">Chọn mức độ</option>
              {DIFFICULTY_LEVELS.map(lvl => (
                <option key={lvl.value} value={lvl.value}>
                  {lvl.label}
                </option>
              ))}
            </select>
          </div>
        </section>

        {/* Test Cases - STEP 1: Sinh Input từ AI */}
        {step === 1 && (
          <section className="form-section">
            <h3>Bước 1: Sinh Input từ AI</h3>

            <div className="upload-box">
              <div className="upload-row">
                <input
                  type="file"
                  accept=".xlsx,.xls"
                  onChange={(e) => setExcelFile(e.target.files[0])}
                  id="excel-upload"
                  hidden
                />
                <label htmlFor="excel-upload" className="file-btn">
                  <FileSpreadsheet size={16} />
                  {excelFile?.name || 'Chọn file Excel'}
                </label>
                <button
                  onClick={() => alert('Upload file Excel test case!')}
                  disabled={!excelFile}
                  className="btn btn-upload"
                >
                  <Upload size={16} />
                  Upload
                </button>
              </div>

              <div className="divider">hoặc</div>

              <button
                onClick={handleGenerateAI}
                disabled={aiGenerating}
                className="btn btn-ai"
              >
                {aiGenerating ? (
                  <>
                    <div className="spinner" /> Đang sinh input AI...
                  </>
                ) : (
                  <>
                    <Sparkles size={16} /> Sinh Input bằng AI
                  </>
                )}
              </button>
            </div>
          </section>
        )}

        {/* STEP 2: Kiểm tra và chỉnh sửa input */}
        {step === 2 && (
          <section className="form-section">
            <h3>Bước 2: Kiểm tra Input</h3>
            <p className="step-description">
              📋 Kiểm tra xem các input dưới đây có đúng constraint không. Bạn có thể chỉnh sửa hoặc xóa input.
            </p>

            <div className="inputs-review">
              {editingInputs.map((inputItem, index) => (
                <div key={index} className="input-card">
                  <div className="input-header">
                    <label>Input #{index + 1}</label>
                    <button
                      onClick={() => handleDeleteInput(index)}
                      className="btn-delete"
                      title="Xóa input này"
                    >
                      <X size={18} />
                    </button>
                  </div>
                  <textarea
                    value={inputItem.input}
                    onChange={(e) => handleEditInput(index, e.target.value)}
                    rows={4}
                    placeholder="Nhập input..."
                  />
                </div>
              ))}
            </div>

            <div className="step-actions">
              <button
                onClick={() => {
                  setStep(1);
                  setAiInputs([]);
                  setEditingInputs([]);
                }}
                className="btn btn-secondary"
              >
                Sinh lại
              </button>
              <button
                onClick={handleProceedToCode}
                className="btn btn-primary"
              >
                Tiếp theo → Nhập Source Code
              </button>
            </div>
          </section>
        )}

        {/* STEP 3: Nhập source code */}
        {step === 3 && (
          <section className="form-section">
            <h3>Bước 3: Nhập Source Code</h3>
            <p className="step-description">
              💻 Chọn ngôn ngữ lập trình và nhập source code. Hệ thống sẽ chạy code này trên các input bên trên.
            </p>

            <div className="code-setup">
              <div className="form-field">
                <label>Chọn ngôn ngữ lập trình</label>
                <select
                  value={languageId}
                  onChange={(e) => setLanguageId(e.target.value)}
                  className="language-select"
                >
                  {LANGUAGES.map(lang => (
                    <option key={lang.id} value={lang.id}>
                      {lang.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="code-editor-container">
              <div className="code-editor-label">
                <label>Source Code</label>
              </div>
              <textarea
                value={sourceCode}
                onChange={(e) => setSourceCode(e.target.value)}
                rows={12}
                placeholder="Nhập source code của bạn tại đây..."
                className="code-editor"
              />
            </div>

            <div className="step-actions">
              <button
                onClick={() => setStep(2)}
                className="btn btn-secondary"
              >
                ← Quay lại
              </button>
              <button
                onClick={handleRunTests}
                disabled={isRunning}
                className="btn btn-primary"
              >
                {isRunning ? (
                  <>
                    <div className="spinner" /> Đang chạy test...
                  </>
                ) : (
                  <>
                    <Play size={16} /> Chạy Test Cases
                  </>
                )}
              </button>
            </div>
          </section>
        )}

        {/* STEP 4: Hiển thị kết quả test */}
        {step === 4 && (
          <section className="form-section">
            <h3>Bước 4: Kết quả Test Cases</h3>
            <p className="step-description">
              ✅ Test cases đã được chạy! Kiểm tra input/output dưới đây.
            </p>

            {testCases.length > 0 && (
              <TestCase
                testCases={testCases}
                onRemoveTestCase={(i) => {
                  setTestCases(prev => prev.filter((_, idx) => idx !== i));
                }}
              />
            )}

            <div className="step-actions">
              <button
                onClick={() => {
                  setStep(3);
                  setTestCases([]);
                }}
                className="btn btn-secondary"
              >
                ← Chỉnh sửa Code
              </button>
              <button
                onClick={handleRunTests}
                disabled={isRunning}
                className="btn btn-secondary"
              >
                Chạy lại
              </button>
            </div>
          </section>
        )}

        {/* Submit */}
        {step === 4 && testCases.length > 0 && (
          <div className="form-actions">
            <button onClick={handleSubmit} className="btn btn-primary">
              <Save size={18} /> Lưu Problem Contest
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default ProblemContest;