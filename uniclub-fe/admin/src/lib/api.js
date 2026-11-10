import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

// Helper function to get auth token
const getAuthToken = () => {
  return localStorage.getItem('token')
}

// Helper function to get headers with auth
const getAuthHeaders = () => {
  const token = getAuthToken()
  return {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` })
  }
}

// Configure axios instance
const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Add request interceptor to attach auth token
axiosInstance.interceptors.request.use(
  (config) => {
    const token = getAuthToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Add response interceptor for error handling
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid, redirect to login
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = `${import.meta.env.BASE_URL.replace(/\/+$/, '')}/login`
    }
    
    console.error("❌ API Error:", error.response?.data || error.message)
    return Promise.reject(error)
  }
)

async function fetchAPI(endpoint, options = {}) {
  try {
    const response = await axiosInstance({
      url: endpoint,
      ...options
    })
    return response.data
  } catch (error) {
    throw error
  }
}

export const api = {
  // Generic CRUD
  list: async (resource, params = {}) => {
    const data = await fetchAPI(`/${resource}`, { 
      method: 'GET',
      params 
    })
    return data || []
  },

  get: async (resource, id) => {
    const data = await fetchAPI(`/${resource}/${id}`, { 
      method: 'GET' 
    })
    return data
  },

  create: async (resource, payload) => {
    return await fetchAPI(`/${resource}`, {
      method: "POST",
      data: payload,
    })
  },

  update: async (resource, id, payload) => {
    return await fetchAPI(`/${resource}/${id}`, {
      method: "PUT",
      data: payload,
    })
  },

  delete: async (resource, id) => {
    return await fetchAPI(`/${resource}/${id}`, { 
      method: "DELETE" 
    })
  },

  // Special endpoints
  searchVariants: async (keyword) => {
    const data = await fetchAPI(`/variants/search`, { 
      method: 'GET',
      params: { q: keyword }
    })
    return data || []
  },

  approveGrn: async (id) => {
    return await fetchAPI(`/grn-headers/${id}`, { 
      method: "PUT",
      data: { status: "COMPLETED" }
    })
  },

  getGrnDetails: async (id) => {
    const data = await fetchAPI(`/grn-details/grn-header/${id}`, {
      method: 'GET'
    })
    return data || []
  },

  // Order specific API
  updateOrderStatus: async (orderId, data) => {
    return await fetchAPI(`/orders/${orderId}/status`, {
      method: "PUT",
      data: data
    })
  },
}
