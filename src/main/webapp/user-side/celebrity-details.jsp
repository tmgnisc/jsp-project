<%@page import="model.Movie"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Celebrity" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Celebrity Detail - Nepal Navigator</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background-color: #f5f7fa;
            color: #1f2937;
        }
        .actor-image {
            width: 300px;
            height: 400px;
            object-fit: cover;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: transform 0.3s ease;
        }
        .actor-image:hover {
            transform: scale(1.02);
        }
        .card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0, 43, 91, 0.1);
            overflow: hidden;
            transition: box-shadow 0.3s ease;
        }
        .card:hover {
            box-shadow: 0 6px 16px rgba(0, 43, 91, 0.15);
        }
        .section-title {
            color: #002B5B;
            font-weight: 600;
            border-bottom: 2px solid #F4A300;
            padding-bottom: 4px;
            display: inline-block;
        }
        .film-item a {
            transition: color 0.3s ease;
        }
        .film-item a:hover {
            color: #A31621;
            text-decoration: underline;
        }
        @media (max-width: 768px) {
            .actor-image {
                width: 100%;
                height: auto;
            }
            .grid-cols-2 {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body class="bg-gray-50">
    <!-- Navigation -->
    <nav class="bg-[#002B5B] text-white shadow-lg">
        <div class="container mx-auto px-4 py-4 flex justify-between items-center">
            <a href="${pageContext.request.contextPath}/index" class="text-2xl font-bold text-[#F4A300] flex items-center">
                <i class="fas fa-compass mr-2"></i> Nepal Navigator
            </a>
            <div class="hidden md:flex space-x-6 items-center">
                <a href="${pageContext.request.contextPath}/index" class="hover:text-[#F4A300] transition duration-200">Home</a>
                <a href="${pageContext.request.contextPath}/foods" class="hover:text-[#F4A300] transition duration-200">Foods</a>
                <a href="${pageContext.request.contextPath}/attractions" class="hover:text-[#F4A300] transition duration-200">Attractions</a>
                <a href="${pageContext.request.contextPath}/music" class="hover:text-[#F4A300] transition duration-200">Music</a>
                <a href="${pageContext.request.contextPath}/movies" class="hover:text-[#F4A300] transition duration-200">Movies</a>
                <a href="${pageContext.request.contextPath}/sports" class="hover:text-[#F4A300] transition duration-200">Sports</a>
                <% String username = (String) session.getAttribute("username");
                   if (username != null) { %>
                    <span class="text-white bg-[#F4A300] px-2 py-1 rounded-full text-sm mr-2">Welcome, <%= username %></span>
                    <a href="${pageContext.request.contextPath}/logout" class="hover:text-[#F4A300] transition duration-200">Logout</a>
                <% } else { %>
                    <a href="${pageContext.request.contextPath}/login" class="hover:text-[#F4A300] transition duration-200">Login/Register</a>
                <% } %>
            </div>
            <button class="md:hidden text-2xl focus:outline-none">
                <i class="fas fa-bars"></i>
            </button>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="max-w-7xl mx-auto px-4 py-12">
        <!-- Breadcrumb -->
        <nav class="flex mb-8" aria-label="Breadcrumb">
            <ol class="flex items-center space-x-2 text-sm text-gray-500">
                <li class="flex items-center">
                    <a href="${pageContext.request.contextPath}/index" class="hover:text-[#F4A300] transition duration-200">Home</a>
                    <i class="fas fa-chevron-right mx-2"></i>
                </li>
                <li class="flex items-center">
                    <a href="${pageContext.request.contextPath}/movies" class="hover:text-[#F4A300] transition duration-200">Movies</a>
                    <i class="fas fa-chevron-right mx-2"></i>
                </li>
                <li>
                    <span>Celebrity Detail</span>
                </li>
            </ol>
        </nav>

        <!-- Celebrity Detail -->
        <% 
            Celebrity celebrity = (Celebrity) request.getAttribute("celebrity");
            String error = (String) request.getAttribute("error");
            if (error != null) { %>
                <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg mb-6" role="alert">
                    <p><%= error %></p>
                </div>
            <% } else if (celebrity == null) { %>
                <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg mb-6" role="alert">
                    <p>Error: Celebrity details not found.</p>
                </div>
            <% } else { %>
            <div class="card">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-8 p-6 md:p-8">
                    <!-- Celebrity Image -->
                    <div class="flex justify-center">
                        <img src="${pageContext.request.contextPath}<%= celebrity.getImage() != null && !celebrity.getImage().isEmpty() ? celebrity.getImage() : "/images/placeholder.jpg" %>" 
                             alt="<%= celebrity.getName() != null ? celebrity.getName() : "Celebrity" %>" 
                             class="actor-image" 
                             onerror="this.src='https://via.placeholder.com/300x400'">
                    </div>
                    <!-- Celebrity Information -->
                    <div class="space-y-6">
                        <h1 class="text-4xl font-bold text-[#002B5B] tracking-wide"><%= celebrity.getName() != null ? celebrity.getName() : "Unknown Celebrity" %></h1>
                        <div class="space-y-6">
                            <div>
                                <h2 class="section-title">Biography</h2>
                                <p class="text-gray-600 mt-3 leading-relaxed"><%= celebrity.getBio() != null ? celebrity.getBio() : "No biography available." %></p>
                            </div>
                            <div>
                                <h2 class="section-title">Filmography</h2>
                                <div class="mt-3 space-y-3">
                                    <% 
                                        @SuppressWarnings("unchecked")
                                        List<Movie> movies = (List<Movie>) request.getAttribute("movies");
                                        if (movies != null && !movies.isEmpty()) {
                                            for (Movie movie : movies) { %>
                                                <div class="film-item flex items-center space-x-4">
                                                    <a href="${pageContext.request.contextPath}/movie-detail?id=<%= movie.getId() %>" 
                                                       class="text-[#F4A300] hover:text-[#A31621] font-medium">
                                                        <%= movie.getTitle() != null ? movie.getTitle() : "Untitled Movie" %>
                                                    </a>
                                                    <span class="text-gray-500 text-sm">(<%= movie.getGenre() != null ? movie.getGenre() : "N/A" %>)</span>
                                                </div>
                                            <% }
                                        } else { %>
                                            <p class="text-gray-500">No movies found for this celebrity.</p>
                                        <% } %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <% } %>
    </div>

    <!-- Footer -->
    <footer class="bg-[#002B5B] text-white mt-12">
        <div class="max-w-7xl mx-auto px-4 py-10">
            <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
                <div>
                    <h3 class="text-xl font-bold text-[#F4A300] mb-4">Nepal Navigator</h3>
                    <p class="text-gray-300 leading-relaxed">Explore the rich culture, cuisine, and natural beauty of Nepal with our comprehensive guide.</p>
                </div>
                <div>
                    <h4 class="text-lg font-semibold mb-4">Quick Links</h4>
                    <ul class="space-y-2">
                        <li><a href="${pageContext.request.contextPath}/foods" class="text-gray-300 hover:text-[#F4A300] transition duration-200">Foods</a></li>
                        <li><a href="${pageContext.request.contextPath}/attractions" class="text-gray-300 hover:text-[#F4A300] transition duration-200">Attractions</a></li>
                        <li><a href="${pageContext.request.contextPath}/music" class="text-gray-300 hover:text-[#F4A300] transition duration-200">Music</a></li>
                        <li><a href="${pageContext.request.contextPath}/movies" class="text-gray-300 hover:text-[#F4A300] transition duration-200">Movies</a></li>
                        <li><a href="${pageContext.request.contextPath}/sports" class="text-gray-300 hover:text-[#F4A300] transition duration-200">Sports</a></li>
                    </ul>
                </div>
                <div>
                    <h4 class="text-lg font-semibold mb-4">Contact Us</h4>
                    <ul class="space-y-2 text-gray-300">
                        <li><i class="fas fa-envelope mr-2"></i> info@nepalnavigator.com</li>
                        <li><i class="fas fa-phone mr-2"></i> +977 1234567890</li>
                        <li><i class="fas fa-map-marker-alt mr-2"></i> Kathmandu, Nepal</li>
                    </ul>
                </div>
                <div>
                    <h4 class="text-lg font-semibold mb-4">Follow Us</h4>
                    <div class="flex space-x-4">
                        <a href="#" class="text-gray-300 hover:text-[#F4A300] transition duration-200">
                            <i class="fab fa-facebook-f text-lg"></i>
                        </a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300] transition duration-200">
                            <i class="fab fa-twitter text-lg"></i>
                        </a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300] transition duration-200">
                            <i class="fab fa-instagram text-lg"></i>
                        </a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300] transition duration-200">
                            <i class="fab fa-youtube text-lg"></i>
                        </a>
                    </div>
                </div>
            </div>
            <div class="border-t border-gray-700 mt-8 pt-6 text-center text-gray-400">
                <p>© 2025 Nepal Navigator. All rights reserved.</p>
            </div>
        </div>
    </footer>
</body>
</html>