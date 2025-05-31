<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Music, model.MusicComment, model.Celebrity, java.util.List, java.time.LocalDateTime, java.time.ZoneId, java.time.temporal.ChronoUnit, java.time.format.DateTimeParseException" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Music Detail - Nepal Navigator</title>
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
        .celebrity-image { 
            width: 80px; 
            height: 80px; 
            object-fit: cover; 
            border-radius: 50%; 
        }
        .artists-section { 
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
                            <a href="${pageContext.request.contextPath}/music" class="text-gray-600 hover:text-[#F4A300]">Music</a>
                        </div>
                    </li>
                    <li>
                        <div class="flex items-center">
                            <i class="fas fa-chevron-right text-gray-400 mx-2"></i>
                            <span class="text-gray-500">${music.artistName != null ? music.artistName : "Artist Name"}</span>
                        </div>
                    </li>
                </ol>
            </nav>
        </div>

        <!-- Music Detail -->
        <% Music music = (Music) request.getAttribute("music");
           @SuppressWarnings("unchecked")
           List<Celebrity> celebrities = (List<Celebrity>) request.getAttribute("celebrities");
           if (music == null) { %>
            <p class="text-red-500">Error: Music details not found.</p>
        <% } else { %>
        <div class="bg-white rounded-lg shadow-lg overflow-hidden">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-8 p-8">
                <!-- Main Image -->
                <div>
                    <img src="${pageContext.request.contextPath}<%= music.getImage() != null ? music.getImage() : "/images/placeholder.jpg" %>" 
                         alt="<%= music.getArtistName() != null ? music.getArtistName() : "Music Image" %>" 
                         class="w-full h-96 object-cover rounded-lg" 
                         onerror="this.src='https://via.placeholder.com/500'">
                </div>

                <!-- Music Information -->
                <div class="space-y-6">
                    <div>
                        <h1 class="text-3xl font-bold text-[#002B5B]"><%= music.getArtistName() != null ? music.getArtistName() : "Unknown Artist" %></h1>
                        <p class="text-gray-600 mt-2"><%= music.getDescription() != null ? music.getDescription() : "No description available." %></p>
                    </div>
                    <div>
                        <p class="text-gray-600"><strong>Genre:</strong> <%= music.getGenre() != null ? music.getGenre() : "N/A" %></p>
                        <p class="text-gray-600"><strong>Formation Year:</strong> <%= music.getFormationYear() > 0 ? music.getFormationYear() : "N/A" %></p>
                    </div>
                    <div>
                        <p class="text-gray-600"><strong>Popular Songs:</strong></p>
                        <p class="text-gray-600"><%= music.getPopularSongs() != null ? music.getPopularSongs() : "No popular songs listed." %></p>
                    </div>
                    <div>
                        <p class="text-gray-600"><strong>Achievements:</strong></p>
                        <p class="text-gray-600"><%= music.getAchievements() != null ? music.getAchievements() : "No achievements listed." %></p>
                    </div>
                    <div>
                        <p class="text-gray-600"><strong>YouTube Channel:</strong></p>
                        <a href="<%= music.getYoutubeChannelUrl() != null ? music.getYoutubeChannelUrl() : "#" %>" 
                           target="_blank" 
                           class="text-[#F4A300] hover:text-[#A31621]">
                            <%= music.getYoutubeChannelUrl() != null ? "Visit YouTube Channel" : "Not available" %>
                        </a>
                    </div>
                </div>
            </div>

            <!-- Associated Artists -->
            <div class="border-t border-gray-200 p-8 artists-section">
                <h2 class="text-2xl font-semibold text-[#002B5B] mb-6">Artists</h2>
                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    <% if (celebrities != null && !celebrities.isEmpty()) {
                          for (Celebrity celebrity : celebrities) { %>
                    <div class="bg-white rounded-lg shadow overflow-hidden">
                        <div class="flex items-center p-4">
                            <img src="${pageContext.request.contextPath}<%= celebrity.getImage() != null ? celebrity.getImage() : "/images/placeholder.jpg" %>" 
                                 alt="<%= celebrity.getName() != null ? celebrity.getName() : "Celebrity Image" %>" 
                                 class="celebrity-image mr-4" 
                                 onerror="this.src='https://via.placeholder.com/80'">
                            <div>
                                <h3 class="text-lg font-semibold text-[#002B5B] mb-1"><%= celebrity.getName() != null ? celebrity.getName() : "Unknown Celebrity" %></h3>
                                <a href="${pageContext.request.contextPath}/celebrity-details?id=<%= celebrity.getId() %>" 
                                   class="text-[#F4A300] hover:text-[#A31621] text-sm">
                                    View Profile →
                                </a>
                            </div>
                        </div>
                    </div>
                    <%     }
                       } else { %>
                        <p class="text-gray-500">No associated artists found.</p>
                    <% } %>
                </div>
            </div>

            <!-- Comments Section -->
            <div class="border-t border-gray-200 p-8">
                <h2 class="text-2xl font-semibold text-[#002B5B] mb-6">Comments</h2>
                <!-- Add Comment Form -->
                <form action="${pageContext.request.contextPath}/music-detail" method="post" class="mb-8">
                    <input type="hidden" name="musicId" value="<%= music.getId() %>">
                    <div class="flex items-start space-x-4">
                        <div class="flex-1">
                            <textarea name="commentText" rows="3" 
                                      class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#F4A300] focus:border-transparent" 
                                      placeholder="Add your comment..." 
                                      required></textarea>
                        </div>
                        <button type="submit" 
                                class="bg-[#F4A300] text-white px-6 py-2 rounded-md hover:bg-[#A31621] transition duration-300">
                            Post Comment
                        </button>
                    </div>
                    <% String error = (String) request.getAttribute("error");
                       if (error != null) { %>
                        <p class="text-red-500 mt-2"><%= error %></p>
                    <% } %>
                </form>

                <!-- Display Comments -->
                <div class="space-y-6">
                    <% @SuppressWarnings("unchecked")
                       List<MusicComment> comments = (List<MusicComment>) request.getAttribute("comments");
                       if (comments != null && !comments.isEmpty()) {
                           for (MusicComment comment : comments) {
                               LocalDateTime commentTime = null;
                               try {
                                   commentTime = LocalDateTime.parse(comment.getCreatedAt(), 
                                       java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                               } catch (DateTimeParseException e) {
                                   commentTime = LocalDateTime.now();
                               }
                               LocalDateTime now = LocalDateTime.now();
                               long hours = ChronoUnit.HOURS.between(commentTime, now);
                               long days = ChronoUnit.DAYS.between(commentTime, now);
                               String timeAgo = days > 0 ? days + " day" + (days > 1 ? "s" : "") + " ago" : 
                                                hours > 0 ? hours + " hour" + (hours > 1 ? "s" : "") + " ago" : 
                                                "just now";
                    %>
                    <div class="bg-gray-50 rounded-lg p-4">
                        <div class="flex items-start space-x-4">
                            <img src="https://ui-avatars.com/api/?name=<%= comment.getUsername() != null ? comment.getUsername() : "User" %>&background=002B5B&color=fff" 
                                 alt="User Avatar" 
                                 class="w-10 h-10 rounded-full">
                            <div class="flex-1">
                                <div class="flex justify-between items-center">
                                    <h3 class="text-sm font-semibold text-[#002B5B]"><%= comment.getUsername() != null ? comment.getUsername() : "Anonymous" %></h3>
                                    <span class="text-xs text-gray-500"><%= timeAgo %></span>
                                </div>
                                <p class="text-gray-600 mt-1"><%= comment.getCommentText() != null ? comment.getCommentText() : "No comment text." %></p>
                            </div>
                        </div>
                    </div>
                    <%     }
                       } else { %>
                        <p class="text-gray-500">No comments yet. Be the first to comment!</p>
                    <% } %>
                </div>
            </div>
        </div>
        <% } %>
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
</body>
</html>