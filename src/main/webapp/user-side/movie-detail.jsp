<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.Movie, model.MovieComment, model.Celebrity, java.time.LocalDateTime, java.time.ZoneId, java.time.temporal.ChronoUnit, java.time.format.DateTimeParseException" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Movie Detail - Nepal Navigator</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body { 
            font-family: 'Poppins', sans-serif;
            background-color: #f5f7fa;
        }
        .image-gallery img { 
            transition: transform 0.3s ease; 
        }
        .image-gallery img:hover { 
            transform: scale(1.05); 
        }
        .thumbnail-active {
            border: 2px solid #F4A300;
            opacity: 1;
        }
        .thumbnail {
            opacity: 0.7;
            cursor: pointer;
        }
        .thumbnail:hover {
            opacity: 1;
        }
        .celebrity-image { 
            width: 80px; 
            height: 80px; 
            object-fit: cover; 
            border-radius: 50%; 
        }
        .actors-section { 
            background-color: #f9fafb; 
            border-left: 4px solid #F4A300; 
            padding-left: 1rem; 
        }
    </style>
</head>
<body class="bg-gray-50">
    <!-- Navigation -->
    <nav class="bg-[#002B5B] text-white shadow-lg">
        <div class="container mx-auto px-4">
            <div class="flex justify-between items-center py-4">
                <a href="${pageContext.request.contextPath}/index" class="text-2xl font-bold text-[#F4A300]">Nepal Navigator</a>
                <div class="hidden md:flex space-x-6">
                    <a href="${pageContext.request.contextPath}/index" class="hover:text-[#F4A300]">Home</a>
                    <a href="${pageContext.request.contextPath}/foods" class="hover:text-[#F4A300]">Foods</a>
                    <a href="${pageContext.request.contextPath}/attractions" class="hover:text-[#F4A300]">Attractions</a>
                    <a href="${pageContext.request.contextPath}/music" class="hover:text-[#F4A300]">Music</a>
                    <a href="${pageContext.request.contextPath}/movies" class="hover:text-[#F4A300]">Movies</a>
                    <a href="${pageContext.request.contextPath}/sports" class="hover:text-[#F4A300]">Sports</a>
                    <% String username = (String) session.getAttribute("username");
                       if (username != null) { %>
                        <span class="text-white">Welcome, <%= username %>!</span>
                        <a href="${pageContext.request.contextPath}/logout" class="hover:text-[#F4A300]">Logout</a>
                    <% } else { %>
                        <a href="${pageContext.request.contextPath}/login" class="hover:text-[#F4A300]">Login/Register</a>
                    <% } %>
                </div>
                <button class="md:hidden">
                    <i class="fas fa-bars text-2xl"></i>
                </button>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="max-w-7xl mx-auto px-4 py-8">
        <% Movie movie = (Movie) request.getAttribute("movie");
           if (movie == null) {
               response.sendRedirect(request.getContextPath() + "/movies");
               return;
           }
           @SuppressWarnings("unchecked")
           List<Celebrity> celebrities = (List<Celebrity>) request.getAttribute("celebrities");
        %>
        <!-- Breadcrumb -->
        <div class="mb-6">
            <nav class="flex" aria-label="Breadcrumb">
                <ol class="inline-flex items-center space-x-1 md:space-x-3">
                    <li class="inline-flex items-center">
                        <a href="${pageContext.request.contextPath}/index" class="text-gray-600 hover:text-[#F4A300]">Home</a>
                    </li>
                    <li>
                        <div class="flex items-center">
                            <i class="fas fa-chevron-right text-gray-400 mx-2"></i>
                            <a href="${pageContext.request.contextPath}/movies" class="text-gray-600 hover:text-[#F4A300]">Movies</a>
                        </div>
                    </li>
                    <li>
                        <div class="flex items-center">
                            <i class="fas fa-chevron-right text-gray-400 mx-2"></i>
                            <span class="text-gray-500"><%= movie.getTitle() != null ? movie.getTitle() : "Unknown" %></span>
                        </div>
                    </li>
                </ol>
            </nav>
        </div>

        <!-- Movie Detail -->
        <div class="bg-white rounded-lg shadow-lg overflow-hidden">
            <!-- Image Gallery -->
            <div class="p-8 image-gallery">
                <div class="grid grid-cols-1 md:grid-cols-5 gap-4">
                    <div class="relative h-96 rounded-lg overflow-hidden md:col-span-3">
                        <img id="main-image" 
                             src="${pageContext.request.contextPath}<%= movie.getImage() != null ? movie.getImage() : "/images/placeholder.jpg" %>" 
                             alt="<%= movie.getTitle() != null ? movie.getTitle() : "Movie Image" %>" 
                             class="w-full h-full object-cover"
                             onerror="this.src='https://via.placeholder.com/800'">
                        <div class="absolute top-4 right-4">
                            <button class="bg-white p-2 rounded-full shadow-lg hover:bg-gray-100">
                                <i class="fas fa-heart text-red-500"></i>
                            </button>
                        </div>
                    </div>
                    <div class="grid grid-cols-4 gap-4 md:col-span-2">
                        <% for (int i = 0; i < 4; i++) { %>
                            <div class="h-24 rounded-lg overflow-hidden">
                                <img src="${pageContext.request.contextPath}<%= movie.getImage() != null ? movie.getImage() : "/images/placeholder.jpg" %>" 
                                     alt="<%= movie.getTitle() != null ? movie.getTitle() : "Movie Image" %>" 
                                     class="w-full h-full object-cover cursor-pointer thumbnail <%= i == 0 ? "thumbnail-active" : "" %>"
                                     onclick="changeMainImage(this.src)"
                                     onerror="this.src='https://via.placeholder.com/200'">
                            </div>
                        <% } %>
                    </div>
                </div>
            </div>

            <!-- Movie Information -->
            <div class="p-8">
                <div class="space-y-6">
                    <div>
                        <h1 class="text-3xl font-bold text-[#002B5B]"><%= movie.getTitle() != null ? movie.getTitle() : "Unknown Movie" %></h1>
                        <div class="flex items-center mt-2">
                            <div class="flex text-yellow-400">
                                <% float rating = movie.getRating();
                                   int fullStars = (int) rating;
                                   boolean hasHalfStar = rating - fullStars >= 0.5;
                                   for (int i = 0; i < fullStars; i++) { %>
                                       <i class="fas fa-star"></i>
                                   <% }
                                   if (hasHalfStar) { %>
                                       <i class="fas fa-star-half-alt"></i>
                                   <% }
                                   for (int i = fullStars + (hasHalfStar ? 1 : 0); i < 5; i++) { %>
                                       <i class="far fa-star"></i>
                                   <% } %>
                            </div>
                            <span class="text-gray-600 ml-2"><%= movie.getRating() %>/5</span>
                        </div>
                    </div>

                    <div class="space-y-4">
                        <div>
                            <h2 class="text-xl font-semibold text-[#002B5B]">Plot Summary</h2>
                            <p class="text-gray-600 mt-2"><%= movie.getDescription() != null ? movie.getDescription() : "No description available." %></p>
                        </div>

                        <div>
                            <h2 class="text-xl font-semibold text-[#002B5B]">Movie Details</h2>
                            <div class="mt-2 space-y-2">
                                <div class="flex items-center text-gray-600">
                                    <i class="fas fa-tag w-6"></i>
                                    <span>Genre: <%= movie.getGenre() != null ? movie.getGenre() : "Not specified" %></span>
                                </div>
                            </div>
                        </div>

                        <div class="actors-section">
                            <h2 class="text-xl font-semibold text-[#002B5B]">Actors</h2>
                            <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4 mt-2">
                                <% if (celebrities != null && !celebrities.isEmpty()) {
                                       for (Celebrity celeb : celebrities) { %>
                                        <div class="flex items-center space-x-4 p-2 bg-white rounded-lg shadow hover:shadow-md transition-shadow">
                                            <a href="${pageContext.request.contextPath}/celebrity-details?id=<%= celeb.getId() %>" 
                                               class="flex items-center space-x-4">
                                                <img src="${pageContext.request.contextPath}<%= celeb.getImage() != null ? celeb.getImage() : "/images/placeholder.jpg" %>" 
                                                     alt="<%= celeb.getName() != null ? celeb.getName() : "Celebrity" %>" 
                                                     class="celebrity-image" 
                                                     onerror="this.src='https://via.placeholder.com/80'">
                                                <span class="text-gray-800 font-medium"><%= celeb.getName() != null ? celeb.getName() : "Unknown" %></span>
                                            </a>
                                        </div>
                                <% }
                                   } else { %>
                                    <p class="text-gray-600">No actors listed for this movie.</p>
                                <% } %>
                            </div>
                        </div>

                        <div>
                            <h2 class="text-xl font-semibold text-[#002B5B]">Links</h2>
                            <div class="mt-2 space-y-2">
                                <div class="flex items-center text-gray-600">
                                    <i class="fab fa-youtube w-6"></i>
                                    <a href="<%= movie.getTrailerUrl() != null ? movie.getTrailerUrl() : "#" %>" class="text-[#F4A300] hover:text-[#A31621]">
                                        Watch Trailer
                                    </a>
                                </div>
                                <div class="flex items-center text-gray-600">
                                    <i class="fas fa-ticket-alt w-6"></i>
                                    <a href="<%= movie.getTicketBookingUrl() != null ? movie.getTicketBookingUrl() : "#" %>" class="text-[#F4A300] hover:text-[#A31621]">
                                        Book Tickets
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Comments Section -->
            <div class="border-t border-gray-200 p-8">
                <h2 class="text-2xl font-semibold text-[#002B5B] mb-6">Comments</h2>
                
                <!-- Comment Form -->
                <div class="mb-8 p-4 bg-white rounded-lg shadow">
                    <form action="${pageContext.request.contextPath}/movie-detail" method="post" class="space-y-4">
                        <input type="hidden" name="movieId" value="<%= movie.getId() %>">
                        <div>
                            <textarea name="commentText" 
                                      class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#F4A300]" 
                                      rows="3" 
                                      placeholder="Write your comment..." 
                                      required></textarea>
                        </div>
                        <div class="flex justify-end">
                            <button type="submit" class="bg-[#F4A300] text-white px-6 py-2 rounded-md hover:bg-[#A31621] transition duration-300">
                                Post Comment
                            </button>
                        </div>
                        <% String error = (String) request.getAttribute("error");
                           if (error != null) { %>
                            <p class="text-red-500 mt-2"><%= error %></p>
                        <% } %>
                    </form>
                </div>

                <!-- Comments List -->
                <div class="space-y-6">
                    <% List<MovieComment> comments = (List<MovieComment>) request.getAttribute("comments");
                       LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kathmandu"));
                       if (comments != null && !comments.isEmpty()) {
                           for (MovieComment comment : comments) {
                               String timeAgo = "Unknown time";
                               try {
                                   if (comment.getCreatedAt() != null) {
                                       LocalDateTime commentTime = LocalDateTime.parse(comment.getCreatedAt(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                           .atZone(ZoneId.of("Asia/Kathmandu")).toLocalDateTime();
                                       long minutesAgo = ChronoUnit.MINUTES.between(commentTime, now);
                                       if (minutesAgo < 60) {
                                           timeAgo = minutesAgo + " minutes ago";
                                       } else if (minutesAgo < 1440) {
                                           long hoursAgo = minutesAgo / 60;
                                           timeAgo = hoursAgo + " hours ago";
                                       } else {
                                           long daysAgo = minutesAgo / 1440;
                                           timeAgo = daysAgo + " days ago";
                                       }
                                   }
                    %>
                    <div class="flex space-x-4">
                        <img src="https://ui-avatars.com/api/?name=<%= comment.getUsername() != null ? comment.getUsername().replace(" ", "+") : "Unknown" %>&background=002B5B&color=fff" 
                             alt="User" 
                             class="w-12 h-12 rounded-full">
                        <div class="flex-1">
                            <div class="flex items-center justify-between">
                                <h3 class="font-semibold text-[#002B5B]"><%= comment.getUsername() != null ? comment.getUsername() : "Anonymous" %></h3>
                                <span class="text-sm text-gray-500"><%= timeAgo %></span>
                            </div>
                            <p class="text-gray-600 mt-1"><%= comment.getCommentText() != null ? comment.getCommentText() : "No comment text" %></p>
                            <div class="flex items-center space-x-4 mt-2">
                                <button class="text-gray-500 hover:text-[#F4A300]">
                                    <i class="far fa-thumbs-up"></i> Like
                                </button>
                                <button class="text-gray-500 hover:text-[#F4A300]">
                                    <i class="far fa-comment"></i> Reply
                                </button>
                            </div>
                        </div>
                    </div>
                    <%      } catch (DateTimeParseException e) { %>
                            <p class="text-red-500 text-sm">Error parsing comment timestamp: <%= comment.getCreatedAt() != null ? comment.getCreatedAt() : "Unknown timestamp" %></p>
                        <% }
                           }
                       } else { %>
                       <div class="text-center text-gray-500">No comments yet. Be the first to comment!</div>
                    <% } %>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bg-[#002B5B] text-white mt-12">
        <div class="max-w-7xl mx-auto px-4 py-8">
            <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
                <div>
                    <h3 class="text-xl font-bold text-[#F4A300] mb-4">Nepal Navigator</h3>
                    <p class="text-gray-300">
                        Discover the rich culture, cuisine, and beauty of Nepal through our comprehensive guide.
                    </p>
                </div>
                <div>
                    <h4 class="text-lg font-semibold mb-4">Quick Links</h4>
                    <ul class="space-y-2">
                        <li><a href="${pageContext.request.contextPath}/index" class="text-gray-300 hover:text-[#F4A300]">Home</a></li>
                        <li><a href="${pageContext.request.contextPath}/foods" class="text-gray-300 hover:text-[#F4A300]">Foods</a></li>
                        <li><a href="${pageContext.request.contextPath}/attractions" class="text-gray-300 hover:text-[#F4A300]">Attractions</a></li>
                        <li><a href="${pageContext.request.contextPath}/music" class="text-gray-300 hover:text-[#F4A300]">Music</a></li>
                        <li><a href="${pageContext.request.contextPath}/movies" class="text-gray-300 hover:text-[#F4A300]">Movies</a></li>
                        <li><a href="${pageContext.request.contextPath}/sports" class="text-gray-300 hover:text-[#F4A300]">Sports</a></li>
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
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]">
                            <i class="fab fa-facebook-f"></i>
                        </a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]">
                            <i class="fab fa-twitter"></i>
                        </a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]">
                            <i class="fab fa-instagram"></i>
                        </a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]">
                            <i class="fab fa-youtube"></i>
                        </a>
                    </div>
                </div>
            </div>
            <div class="border-t border-gray-700 mt-8 pt-8 text-center text-gray-300">
                <p>© 2025 Nepal Navigator. All rights reserved.</p>
            </div>
        </div>
    </footer>

    <!-- JavaScript for Image Switching -->
    <script>
        function changeMainImage(src) {
            const mainImage = document.getElementById('main-image');
            mainImage.src = src;
            const thumbnails = document.querySelectorAll('.thumbnail');
            thumbnails.forEach(thumb => thumb.classList.remove('thumbnail-active'));
            const clickedThumbnail = event.target;
            clickedThumbnail.classList.add('thumbnail-active');
        }
    </script>
</body>
</html>