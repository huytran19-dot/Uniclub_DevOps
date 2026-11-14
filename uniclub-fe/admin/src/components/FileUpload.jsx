import React, { useState, useRef, useEffect } from 'react'
import { api } from '../lib/api'

export default function FileUpload({ 
  label, 
  value, 
  onChange, 
  error, 
  accept = "image/*",
  maxSize = 7 * 1024 * 1024, // 7MB
  className = "",
  singleImage = true // Chỉ cho phép 1 ảnh
}) {
  const [uploading, setUploading] = useState(false)
  const [preview, setPreview] = useState(value || '')
  const fileInputRef = useRef(null)

  // Sync preview with value prop changes
  useEffect(() => {
    console.log('FileUpload value changed:', value)
    setPreview(value || '')
  }, [value])

  const handleRemoveImage = () => {
    setPreview('')
    onChange('')
    if (fileInputRef.current) {
      fileInputRef.current.value = ''
    }
  }

  const handleFileSelect = async (event) => {
    const file = event.target.files[0]
    if (!file) return

    // Validate file type
    if (!file.type.startsWith('image/')) {
      alert('Vui lòng chọn file hình ảnh')
      return
    }

    // Validate file size
    if (file.size > maxSize) {
      alert(`File quá lớn. Kích thước tối đa: ${Math.round(maxSize / 1024 / 1024)}MB`)
      return
    }

    setUploading(true)
    
    try {
      // Upload via centralized Axios instance (auto-attaches JWT token)
      const result = await api.uploadImage(file)
      const imageUrl = result.url

      // Update preview and form value
      setPreview(imageUrl)
      onChange(imageUrl)

    } catch (error) {
      console.error('Upload error:', error)
      const message = error.response?.data?.message
        || error.response?.data
        || error.message
        || 'Upload thất bại'
      alert('Lỗi upload: ' + message)
    } finally {
      setUploading(false)
    }
  }

  return (
    <div className={`mb-4 ${className}`}>
      <label className="block text-sm font-medium text-neutral-700 mb-2">
        {label}
      </label>
      
      {/* File Input */}
      <input
        ref={fileInputRef}
        type="file"
        accept={accept}
        onChange={handleFileSelect}
        className="hidden"
        disabled={uploading || (singleImage && preview)}
      />

      {/* Upload Button */}
      <button
        type="button"
        onClick={() => fileInputRef.current?.click()}
        disabled={uploading || (singleImage && preview)}
        className="w-full px-4 py-2 border-2 border-dashed border-neutral-300 rounded-lg hover:border-blue-500 hover:bg-blue-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {uploading ? (
          <div className="flex items-center justify-center gap-2">
            <div className="w-4 h-4 border-2 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
            <span>Đang upload...</span>
          </div>
        ) : singleImage && preview ? (
          <div className="flex items-center justify-center gap-2 text-green-600">
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
            </svg>
            <span>Đã có hình ảnh</span>
          </div>
        ) : (
          <div className="flex items-center justify-center gap-2">
            <svg className="w-5 h-5 text-neutral-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
            </svg>
            <span>Chọn hình ảnh</span>
          </div>
        )}
      </button>

      {/* Preview */}
      {preview && (
        <div className="mt-3">
          <div className="relative inline-block">
            <img
              src={preview}
              alt="Preview"
              className="w-32 h-32 object-cover rounded-lg border border-neutral-200"
            />
            <button
              type="button"
              onClick={handleRemoveImage}
              className="absolute -top-2 -right-2 w-6 h-6 bg-red-500 text-white rounded-full flex items-center justify-center hover:bg-red-600 transition-colors"
            >
              ×
            </button>
          </div>
          <p className="text-xs text-neutral-500 mt-1">Click để thay đổi hình</p>
        </div>
      )}

      {/* Error Message */}
      {error && (
        <p className="text-red-500 text-sm mt-1">{error}</p>
      )}

      {/* File Info */}
      <p className="text-xs text-neutral-500 mt-1">
        Chấp nhận: JPG, PNG, GIF. Kích thước tối đa: {Math.round(maxSize / 1024 / 1024)}MB
      </p>
    </div>
  )
}
