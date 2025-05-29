<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Movie" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Movies Management - Nepal Navigator</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
        }
        textarea, input[type="text"], input[type="number"], input[type="url"], select {
            width: 100%;
            padding: 8px;
        }
    </style>
</head>
<body class="bg-gray-100">
<%
    // Check if user is authenticated
    String username = (String) session.getAttribute("username");
    String role = (String) session.getAttribute("role");

    if (username == null || role == null) {
        // User is not logged in or role is not set, redirect to login
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    // Check if the user has the "admin" role
    if (!"admin".equalsIgnoreCase(role)) {
        // User is not an admin, redirect to index page
        response.sendRedirect(request.getContextPath() + "/index");
        return;
    }
%>
    <div class="flex h-screen">
        <!-- Sidebar -->
        <div class="w-64 bg-[#002B5B] text-white">
            <div class="p-4">
                <h2 class="text-2xl font-bold text-[#F4A300]">Admin Panel</h2>
            </div>
            <nav class="mt-8">
                <a href="dashboard" class="flex items-center px-4 py-3 bg-[#F4A300] text-white">
                    <i class="fas fa-tachometer-alt w-6"></i>
                    <span>Dashboard</span>
                </a>
                <a href="food-dashboard" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-utensils w-6"></i>
                    <span>Foods</span>
                </a>
                <a href="attraction-dashboard" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-mountain w-6"></i>
                    <span>Attractions</span>
                </a>
                <a href="music-dashboard" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-music w-6"></i>
                    <span>Music</span>
                </a>
                <a href="movie-dashboard" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-film w-6"></i>
                    <span>Movies</span>
                </a>
                <a href="sports-dashboard" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-running w-6"></i>
                    <span>Sports</span>
                </a>
                <a href="user-dashboard" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-users w-6"></i>
                    <span>Users</span>
                </a>
                <a href="index" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-sign-out-alt w-6"></i>
                    <span>Logout</span>
                </a>
            </nav>
        </div>

        <!-- Main Content -->
        <div class="flex-1 overflow-auto">
            <!-- Top Bar -->
            <div class="bg-white shadow-md">
                <div class="flex justify-between items-center px-8 py-4">
                    <h1 class="text-2xl font-semibold text-[#002B5B]">Movies Management</h1>
                    <div class="flex items-center space-x-4">
                        <span class="text-gray-600">Welcome, Admin</span>
                        <img src="https://ui-avatars.com/api/?name=Admin&background=002B5B&color=fff" alt="Admin" class="w-10 h-10 rounded-full">
                    </div>
                </div>
            </div>

            <!-- Notification -->
            <%
                String notify = (String) request.getAttribute("notify");
                if (notify != null && !notify.isEmpty()) {
                    String alertClass = notify.contains("successfully") ? "bg-green-100 border-green-500 text-green-700" : "bg-red-100 border-red-500 text-red-700";
            %>
                <div class="p-8">
                    <div class="<%= alertClass %> border-l-4 p-4 mb-6" role="alert">
                        <p><%= notify %></p>
                    </div>
                </div>
            <%
                }
            %>

            <!-- Content -->
            <div class="p-8">
                <!-- Add New Movie Button -->
                <div class="mb-6">
                    <button onclick="showAddMovieModal()" class="bg-[#F4A300] text-white px-4 py-2 rounded-md hover:bg-[#A31621] transition duration-300">
                        <i class="fas fa-plus mr-2"></i>Add New Movie
                    </button>
                </div>

                <!-- Movies Table -->
                <div class="bg-white rounded-lg shadow-md overflow-hidden">
                    <table class="min-w-full divide-y divide-gray-200">
                        <thead class="bg-gray-50">
                            <tr>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Poster</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Genre</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Rating</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                            </tr>
                        </thead>
                        <tbody class="bg-white divide-y divide-gray-200">
                            <%
                                List<Movie> movieList = (List<Movie>) request.getAttribute("movieList");
                                if (movieList != null) {
                                    for (Movie item : movieList) {
                            %>
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <img src="${pageContext.request.contextPath}<%=item.getImage() != null ? item.getImage() : "/images/placeholder.jpg"%>" alt="Movie" class="w-16 h-16 object-cover rounded" onerror="this.src='https://via.placeholder.com/100'">
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="text-sm font-medium text-gray-900"><%=item.getTitle()%></div>
                                    <div class="text-sm text-gray-500"><%=item.getDescription() != null ? item.getDescription() : ""%></div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800"><%=item.getGenre()%></span>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="text-sm text-gray-900"><%=item.getRating() + "/5"%></div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                    <button onclick="showEditMovieModal(<%=item.getId()%>)" class="text-[#F4A300] hover:text-[#A31621] mr-3">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                   <form action="${pageContext.request.contextPath}/movie-dashboard" method="post" style="display:inline;">
									    <input type="hidden" name="action" value="delete">
									    <input type="hidden" name="id" value="<%=item.getId()%>">
									    <button type="submit" class="text-red-600 hover:text-red-900" onclick="return confirm('Are you sure you want to delete this movie?')">
									        <i class="fas fa-trash"></i>
									    </button>
									</form>
                                </td>
                            </tr>
                            <%
                                    }
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Add/Edit Movie Modal -->
    <div id="movieModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 hidden overflow-y-auto h-full w-full">
        <div class="relative top-20 mx-auto p-5 border w-[600px] shadow-lg rounded-md bg-white">
            <div class="mt-3">
                <h3 class="text-lg font-medium text-[#002B5B] mb-4" id="modalTitle">Add New Movie</h3>
                <form id="movieForm" action="${pageContext.request.contextPath}/movie-dashboard" method="post" enctype="multipart/form-data" class="space-y-4">
                    <input type="hidden" name="action" id="formAction" value="add">
                    <input type="hidden" name="id" id="movieId" value="0">
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Movie Poster</label>
                        <input type="file" name="image" accept="image/*" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Movie Title</label>
                        <input type="text" name="title" id="title" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" required>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Description</label>
                        <textarea name="description" id="description" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" rows="3" required></textarea>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Genre</label>
                        <select name="genre" id="genre" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" required>
                            <option value="">Select Genre</option>
                            <option value="drama">Drama</option>
                            <option value="comedy">Comedy</option>
                            <option value="action">Action</option>
                            <option value="romance">Romance</option>
                            <option value="documentary">Documentary</option>
                        </select>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Rating</label>
                        <input type="number" name="rating" id="rating" step="0.1" min="0" max="5" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" required>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Trailer URL</label>
                        <input type="url" name="trailerUrl" id="trailerUrl" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" required>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Ticket Booking URL</label>
                        <input type="url" name="ticketBookingUrl" id="ticketBookingUrl" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" required>
                    </div>
                    <div class="flex justify-end space-x-3">
                        <button type="button" onclick="closeMovieModal()" class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50">
                            Cancel
                        </button>
                        <button type="submit" class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#F4A300] hover:bg-[#A31621]">
                            Save
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        function showAddMovieModal() {
            document.getElementById('modalTitle').textContent = 'Add New Movie';
            document.getElementById('formAction').value = 'add';
            document.getElementById('movieId').value = '0';
            document.getElementById('movieForm').reset();
            document.getElementById('movieModal').classList.remove('hidden');
        }

        function showEditMovieModal(id) {
            document.getElementById('modalTitle').textContent = 'Edit Movie';
            document.getElementById('formAction').value = 'edit';
            document.getElementById('movieId').value = id;

            fetch('${pageContext.request.contextPath}/movie-dashboard?action=getMovie&id=' + id)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.error) {
                        alert(data.error);
                        return;
                    }
                    document.getElementById('title').value = data.title || '';
                    document.getElementById('description').value = data.description || '';
                    document.getElementById('genre').value = data.genre || '';
                    document.getElementById('rating').value = data.rating || '';
                    document.getElementById('trailerUrl').value = data.trailerUrl || '';
                    document.getElementById('ticketBookingUrl').value = data.ticketBookingUrl || '';
                    document.getElementById('movieModal').classList.remove('hidden');
                })
                .catch(error => {
                    console.error('Error fetching movie:', error);
                    alert('Failed to load movie data.');
                });
        }

        function closeMovieModal() {
            document.getElementById('movieModal').classList.add('hidden');
        }

        function deleteMovie(id) {
            if (confirm('Are you sure you want to delete this movie?')) {
                fetch('${pageContext.request.contextPath}/movie-dashboard', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'action=delete&id=' + id
                })
                .then(response => response.text())
                .then(data => {
                    location.reload();
                })
                .catch(error => {
                    console.error('Error deleting movie:', error);
                    alert('Failed to delete movie.');
                });
            }
        }
    </script>
</body>
</html>